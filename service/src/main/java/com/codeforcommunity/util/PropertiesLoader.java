package com.codeforcommunity.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class will load properties files for you. If you have more properties you want to add, feel
 * free.
 */
public class PropertiesLoader {
  // Our properties directory base path.
  private static final String basePath = "properties/";

  /**
   * Return a {@link Properties} object from the given file in the
   * service/src/main/resources/properties/ directory
   *
   * @param fileName The name of the file (include the .properties suffix).
   * @return The loaded properties.
   */
  private static Properties getProperties(String fileName) {
    // Get the full path (from the root of service/src/main/) to the file.
    String path = basePath + fileName;

    try (InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(path)) {
      Properties prop = new Properties();
      prop.load(input);
      return prop;
    } catch (IOException ex) {
      throw new IllegalArgumentException("Cannot find file: " + path, ex);
    }
  }

  /** Get properties for the database from the db.properties file. */
  public static Properties getDbProperties() {
    return getProperties("db.properties");
  }
}