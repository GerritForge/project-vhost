// Copyright (C) 2017 GerritForge Ltd
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

package com.gerritforge.gerrit.plugins.virtualhost;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectsFilterReponse extends HttpServletResponseWrapper {
  private static final Logger log = LoggerFactory.getLogger(ProjectsFilterReponse.class);

  private final String hostname;
  private final Gson gson;

  public ProjectsFilterReponse(String hostname, HttpServletResponse response, Gson gson) {
    super(response);

    this.hostname = hostname;
    this.gson = gson;
  }

  @Override
  public void setContentLength(int len) {}

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    log.info("getOutputStream");
    return new ProjectsFilterOutputStream(this, hostname, super.getOutputStream(), gson);
  }

  public void setFilteredContentLength(int len) {
    super.setContentLength(len);
  }
}
