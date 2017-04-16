package com.github.shemhazai.mprw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class ResourceLoader {

  private final Logger logger = Logger.getLogger(getClass());

  public String readFile(String fileName) {
    try (InputStream in = getClass().getResourceAsStream(fileName)) {
      BufferedReader reader = new BufferedReader(new InputStreamReader((in)));
      StringBuilder builder = new StringBuilder();
      String line;

      while ((line = reader.readLine()) != null) {
        builder.append(line + '\n');
      }
      return builder.toString();

    } catch (IOException e) {
      logger.warn("Error while reading " + fileName, e);
      return "";
    }
  }
}
