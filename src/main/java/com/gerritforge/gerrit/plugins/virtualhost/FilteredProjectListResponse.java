
package com.gerritforge.gerrit.plugins.virtualhost;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilteredProjectListResponse extends HttpServletResponseWrapper {
  private static final Logger log = LoggerFactory.getLogger(FilteredProjectListResponse.class);
  private final String hostname;
  
  public FilteredProjectListResponse(String hostname, HttpServletResponse response) {
    super(response);
    
    this.hostname = hostname;
  }
  
  @Override
  public void setContentLength(int len) {
  }
  
  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    log.info("getOutputStream");
    return new FilteredServletOutputStream(this, hostname, super.getOutputStream());
  }
  
  public void setFilteredContentLength(int len) {
    super.setContentLength(len);
  }
  
}
