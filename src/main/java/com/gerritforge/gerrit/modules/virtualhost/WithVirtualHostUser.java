// Copyright (C) 2017 GerritForge Ltd.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gerritforge.gerrit.modules.virtualhost;

import java.util.Collection;
import java.util.Set;

import com.google.gerrit.extensions.api.access.GlobalOrPluginPermission;
import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.reviewdb.client.Project.NameKey;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.permissions.PermissionBackend.ForProject;
import com.google.gerrit.server.permissions.PermissionBackend.WithUser;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.gerrit.server.project.DefaultPermissionBackend;
import com.google.gerrit.server.project.RefPatternMatcher;

public class WithVirtualHostUser extends WithUser {
  private final CurrentUser user;
  private final VirtualHostConfig config;
  private final DefaultPermissionBackend defaultPermissionBackend;
  private final WithUser wrapped;

  public interface Factory {
    WithVirtualHostUser get(CurrentUser user);
  }

  @Inject
  WithVirtualHostUser(VirtualHostConfig config, DefaultPermissionBackend defaultBackend,
      @Assisted CurrentUser user) {
    this.config = config;
    this.defaultPermissionBackend = defaultBackend;
    this.user = user;
    this.wrapped = defaultPermissionBackend.user(user);
  }

  @Override
  public ForProject project(NameKey project) {
    if (!config.isEnabled() || matches(project.get(),
        CurrentServerName.get().map(config::getProjects).orElse(config.defaultProjects))) {
      return wrapped.project(project);
    }

    return ForHiddenProject.INSTANCE;
  }

  private boolean matches(String project, String[] projectsPatterns) {
    for (String projectPattern : projectsPatterns) {
      if (RefPatternMatcher.getMatcher(projectPattern).match(project, user)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void check(GlobalOrPluginPermission perm) throws AuthException, PermissionBackendException {
    wrapped.check(perm);
  }

  @Override
  public <T extends GlobalOrPluginPermission> Set<T> test(Collection<T> permSet) throws PermissionBackendException {
    return wrapped.test(permSet);
  }
}
