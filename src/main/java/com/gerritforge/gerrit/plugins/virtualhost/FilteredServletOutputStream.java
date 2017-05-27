
package com.gerritforge.gerrit.plugins.virtualhost;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.httpd.restapi.RestApiServlet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FilteredServletOutputStream extends ServletOutputStream {
  private static final Logger log = LoggerFactory.getLogger(FilteredServletOutputStream.class);

  private final String hostname;
  private final ByteArrayOutputStream outputStream;
  private final ServletOutputStream targetOutputStream;
  private final Gson gson;
  private final FilteredProjectListResponse response;

  public FilteredServletOutputStream(FilteredProjectListResponse response, String hostname,
      ServletOutputStream targetOutputStream) {
    this.response = response;
    this.hostname = hostname;
    this.outputStream = new ByteArrayOutputStream();
    this.targetOutputStream = targetOutputStream;
    this.gson = new Gson();
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setWriteListener(WriteListener listener) {
  }

  @Override
  public void write(int b) throws IOException {
    outputStream.write(b);
  }

  @Override
  public void close() throws IOException {
    outputStream.close();
    byte[] outputBuffer = outputStream.toByteArray();
    log.info("OutputStream size:" + outputBuffer.length);

    String outputString = new String(outputBuffer);

    log.info("OutputStream as string: " + outputString);
    JsonObject jsonObject = gson.fromJson(outputString.substring(RestApiServlet.JSON_MAGIC.length), JsonObject.class);
    JsonObject filteredProjects = new JsonObject();
    for (Map.Entry<String, JsonElement> jsonProject : jsonObject.entrySet()) {
      if (jsonProject.getKey().startsWith(hostname)) {
        filteredProjects.add(jsonProject.getKey(), jsonProject.getValue());
      }
    }
    log.info("FilteredProjects: " + filteredProjects);
    String filteredProjectsBuffer = filteredProjects.toString();
    byte[] filteredProjectsBytes = filteredProjectsBuffer.getBytes();

    response.setFilteredContentLength(filteredProjectsBytes.length + RestApiServlet.JSON_MAGIC.length);

    targetOutputStream.write(RestApiServlet.JSON_MAGIC);
    targetOutputStream.write(filteredProjectsBuffer.getBytes());
    // targetOutputStream.write(outputBuffer);
    targetOutputStream.close();

  }

}
