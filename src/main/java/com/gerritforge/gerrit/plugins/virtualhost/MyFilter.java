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

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

public class MyFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(MyFilter.class);

  @Inject
  public MyFilter() {
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String uri = httpRequest.getRequestURI();
     if (uri.startsWith("/projects/")) {
      String hostname = httpRequest.getServerName();
      String projectFilter = Splitter.on('.').splitToList(hostname).get(0);
      log.info("doFilter(" + projectFilter + ") for " + hostname + uri);
          chain.doFilter(new UncompressedHttpServletRequest((HttpServletRequest) request),
          new FilteredProjectListResponse(projectFilter, (HttpServletResponse) response));
    } else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void destroy() {
  }
}
