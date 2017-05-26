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
import java.util.Collections;
import java.util.Set;

import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.notedb.ChangeNotes;
import com.google.gerrit.server.permissions.ChangePermissionOrLabel;
import com.google.gerrit.server.permissions.PermissionBackend.ForChange;
import com.google.gerrit.server.permissions.PermissionBackend.ForProject;
import com.google.gerrit.server.permissions.PermissionBackend.ForRef;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.gerrit.server.permissions.ProjectPermission;
import com.google.gerrit.server.permissions.RefPermission;
import com.google.gerrit.server.query.change.ChangeData;

public class ForHiddenProject extends ForProject {
  private final ForRef forRef = new HiddenForRef();
  private final ForChange forChange = new HiddenForChange();

  public static ForHiddenProject INSTANCE = new ForHiddenProject();

  public class HiddenForRef extends ForRef {

    @Override
    public ForRef user(CurrentUser user) {
      return this;
    }

    @Override
    public ForChange change(ChangeData cd) {
      return forChange;
    }

    @Override
    public ForChange change(ChangeNotes notes) {
      return forChange;
    }

    @Override
    public void check(RefPermission perm) throws AuthException, PermissionBackendException {
      throwDenied();
    }

    @Override
    public Set<RefPermission> test(Collection<RefPermission> permSet) throws PermissionBackendException {
      return Collections.emptySet();
    }
  }

  public class HiddenForChange extends ForChange {

    @Override
    public CurrentUser user() {
      return null;
    }

    @Override
    public ForChange user(CurrentUser user) {
      return this;
    }

    @Override
    public void check(ChangePermissionOrLabel perm) throws AuthException, PermissionBackendException {
      throwDenied();
    }

    @Override
    public <T extends ChangePermissionOrLabel> Set<T> test(Collection<T> permSet) throws PermissionBackendException {
      return Collections.emptySet();
    }
  }

  @Override
  public ForProject user(CurrentUser user) {
    return this;
  }

  @Override
  public ForRef ref(String ref) {
    return forRef;
  }

  @Override
  public void check(ProjectPermission perm) throws AuthException, PermissionBackendException {
    throwDenied();
  }

  @Override
  public Set<ProjectPermission> test(Collection<ProjectPermission> permSet) throws PermissionBackendException {
    return Collections.emptySet();
  }

  private void throwDenied() throws AuthException {
    throw new AuthException("Resource does not exist");
  }

}
