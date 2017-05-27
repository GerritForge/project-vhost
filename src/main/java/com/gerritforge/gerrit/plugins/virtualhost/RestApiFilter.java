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

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.inject.Inject;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(RestApiFilter.class);
  private Gson gson;

  @Inject
  public RestApiFilter(Gson gson) {
    this.gson = gson;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String uri = httpRequest.getRequestURI();
    if (uri.equalsIgnoreCase("/projects/")) {
      String projectPrefix = getPrefix(httpRequest);

      log.debug("doFilter(" + projectPrefix + ") for " + request.getServerName() + uri);
      chain.doFilter(
          new UnecodedHttpServletRequestWrapper((HttpServletRequest) request),
          new ProjectsFilterReponse(projectPrefix, (HttpServletResponse) response, gson));
    } else {
      chain.doFilter(request, response);
    }
  }

  private String getPrefix(HttpServletRequest httpRequest) {
    String hostname = httpRequest.getServerName();
    return Splitter.on('.').splitToList(hostname).get(0);
  }

  @Override
  public void destroy() {}
}
