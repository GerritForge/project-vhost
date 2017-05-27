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

import static com.google.gerrit.httpd.restapi.RestApiServlet.JSON_MAGIC;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectsFilterOutputStream extends ServletOutputStream {
  private static final Logger log = LoggerFactory.getLogger(ProjectsFilterOutputStream.class);

  private final String prefix;
  private final ByteArrayOutputStream outputStream;
  private final ServletOutputStream targetOutputStream;
  private final Gson gson;
  private final ProjectsFilterReponse response;

  public ProjectsFilterOutputStream(
      ProjectsFilterReponse response,
      String prefix,
      ServletOutputStream targetOutputStream,
      Gson gson) {
    this.response = response;
    this.prefix = prefix;
    this.outputStream = new ByteArrayOutputStream();
    this.targetOutputStream = targetOutputStream;
    this.gson = gson;
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setWriteListener(WriteListener listener) {}

  @Override
  public void write(int b) throws IOException {
    outputStream.write(b);
  }

  @Override
  public void close() throws IOException {
    outputStream.close();
    byte[] outputBytes = outputStream.toByteArray();
    log.debug("Received content with {} bytes", outputBytes.length);
    byte[] outputMagic = new byte[JSON_MAGIC.length];
    System.arraycopy(
        outputBytes, 0, outputMagic, 0, Math.min(outputBytes.length, outputMagic.length));
    if (Arrays.equals(outputMagic, JSON_MAGIC)) {
      outputBytes = filterContent(outputBytes);
      log.debug("Filtered content {} bytes", outputBytes.length);
    }

    response.setFilteredContentLength(outputBytes.length);
    targetOutputStream.write(outputBytes);
    targetOutputStream.close();
  }

  @SuppressWarnings("resource")
  private byte[] filterContent(byte[] outputBytes) throws IOException {
    ByteArrayInputStream is = new ByteArrayInputStream(outputBytes);
    is.read(new byte[JSON_MAGIC.length]);
    InputStreamReader isReader = new InputStreamReader(is, Charsets.UTF_8);
    JsonObject jsonObject = gson.fromJson(isReader, JsonObject.class);

    Set<String> hiddenProjects =
        jsonObject
            .entrySet()
            .stream()
            .map(e -> e.getKey())
            .filter(project -> !project.equals(prefix) && !project.startsWith(prefix + "/"))
            .collect(Collectors.toSet());

    for (String project : hiddenProjects) {
      jsonObject.remove(project);
    }

    ByteArrayOutputStream filteredOs = new ByteArrayOutputStream();
    try (OutputStreamWriter filteredWriter = new OutputStreamWriter(filteredOs)) {
      gson.toJson(jsonObject, filteredWriter);
    }
    filteredOs.close();
    return filteredOs.toByteArray();
  }
}
