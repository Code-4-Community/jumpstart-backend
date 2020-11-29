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

  /**
   * Get properties for the database from the $JDBC_DATABASE_URL system variable or db.properties
   * file.
   */
  public static Properties getDbProperties() {
    // Checks to see if a JDBC_DATABASE_URL property exists as a system variable (that's
    // how Heroku provides the connection info for your db to you).
    String dbUrlProp = System.getenv("JDBC_DATABASE_URL");

    // If it's not null, then set the database.url property in a new Properties object and return.
    if (dbUrlProp != null) {
      Properties prop = new Properties();
      prop.setProperty("database.url", dbUrlProp);
      return prop;
    }

    // If the JDBC_DATABASE_URL property doesn't exist, we're not on Heroku, so get the properties
    // stored in our local properties files and return.
    return getProperties("db.properties");
  }

  /** Get the port to start up on from the $PORT system variable or server.properties file. */
  public static int getServerPort() {
    // Checks to see if a PORT property exists as a system variable (that's how Heroku provides
    // the port to start up on).
    String port = System.getenv("PORT");

    // If it's null, load the property from our server.properties file and get the port value.
    if (port == null) {
      port = getProperties("server.properties").getProperty("server.port");
    }

    return Integer.parseInt(port);
  }
}
