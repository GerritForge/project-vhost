
package com.gerritforge.gerrit.plugins.virtualhost;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class UncompressedHttpServletRequest extends HttpServletRequestWrapper {

  public UncompressedHttpServletRequest(HttpServletRequest request) {
    super(request);
  }

  @Override
  public String getHeader(String name) {
    if (name.equalsIgnoreCase("Accept-Encoding")) {
      return "";
    }
    return super.getHeader(name);
  }
}
