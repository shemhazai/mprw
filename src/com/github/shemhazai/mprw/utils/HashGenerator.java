package com.github.shemhazai.mprw.utils;

import java.security.MessageDigest;

import org.apache.log4j.Logger;

public class HashGenerator {

  private final Logger logger = Logger.getLogger(getClass());
  private final char[] hexArray = "0123456789ABCDEF".toCharArray();

  public String hash(String text) {
    try {
      byte[] bytes = text.getBytes("UTF-8");
      MessageDigest digest = MessageDigest.getInstance("MD5");
      bytes = digest.digest(bytes);
      return convertBytesToHexString(bytes);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return "";
    }
  }

  private String convertBytesToHexString(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }
}
