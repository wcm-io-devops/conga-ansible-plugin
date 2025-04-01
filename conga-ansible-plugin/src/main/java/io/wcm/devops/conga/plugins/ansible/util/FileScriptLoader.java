/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.devops.conga.plugins.ansible.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Loads text content of static file - or the output of dynamic scripts.
 * Currently only python scripts are supported. Python needs to be installed on the machine.
 */
public final class FileScriptLoader {

  private FileScriptLoader() {
    // static methods only
  }

  /**
   * Read content of a file to string. If the file is a (supported) executable script, execute it and return the result.
   * @param file File
   * @return File content or script result
   * @throws IOException I/O error
   */
  public static String readFileToString(File file) throws IOException {
    if (isPythonScript(file)) {
      return executePyhtonScript(file);
    }
    else {
      return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }
  }

  private static boolean isPythonScript(File file) {
    return StringUtils.endsWith(file.getName(), ".py");
  }

  private static String executePyhtonScript(File file) throws IOException {
    // prepare command line
    CommandLine cmd = new CommandLine("python");
    cmd.addArgument(FileUtil.getCanonicalPath(file), true);

    // prepare capturing script output
    ByteArrayOutputStream osOutput = new ByteArrayOutputStream();
    ByteArrayOutputStream osError = new ByteArrayOutputStream();
    PumpStreamHandler streamHeandler = new PumpStreamHandler(osOutput, osError);

    // execute script
    DefaultExecutor executor = DefaultExecutor.builder().get();
    executor.setStreamHandler(streamHeandler);
    try {
      int exitValue = executor.execute(cmd);
      if (exitValue == 0) {
        return osOutput.toString(StandardCharsets.UTF_8.name());
      }
      else {
        throw new IOException("Python script execution of '" + FileUtil.getCanonicalPath(file) + "' failed "
            + "with error code " + exitValue + ":\n"
            + osError.toString(StandardCharsets.UTF_8.name()));
      }
    }
    catch (ExecuteException ex) {
      throw new IOException("Python script execution of '" + FileUtil.getCanonicalPath(file) + "' failed "
          + "with error code " + ex.getExitValue() + ":\n"
          + osError.toString(StandardCharsets.UTF_8.name()), ex);
    }
  }

}
