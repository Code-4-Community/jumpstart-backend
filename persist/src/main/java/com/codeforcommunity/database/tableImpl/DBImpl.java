package com.codeforcommunity.database.tableImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * Our DBImpl class which {@link PostTableDBImpl} and {@link CommentTableDBImpl} will extend from.
 * This is here to provide a few common functions that will be used.
 */
abstract class DBImpl {
  private final String url;
  private final String user;
  private final String password;

  /**
   * The constructor which just calls the {@link DBImpl} super constructor.
   *
   * @param dbProperties A {@link Properties} we expect to contain the values for url, user, and
   *     password for connecting to the database.
   */
  public DBImpl(Properties dbProperties) {
    // Get the url, user, and password values from the provided properties object and make sure
    // they're not null.
    this.url = dbProperties.getProperty("url");
    this.user = dbProperties.getProperty("user");
    this.password = dbProperties.getProperty("password");

    // Make sure nothing is null, otherwise this will cause issues when connecting.
    if (url == null) {
      throw new IllegalArgumentException("Database URL cannot be null.");
    }
    if (user == null) {
      throw new IllegalArgumentException("Database user cannot be null.");
    }
    if (password == null) {
      throw new IllegalArgumentException("Database password cannot be null");
    }
  }

  /**
   * Creates a {@link Connection} to the database. Don't forget to call {@link ResultSet#close()},
   * {@link PreparedStatement#close()}, and {@link Connection#close()} when you're done.
   *
   * @return The newly created Connection.
   * @throws SQLException If there's an issue connecting to the database.
   */
  protected Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  /**
   * Format the given timestamp to a human-readable String.
   *
   * @param time The time to convert to a String.
   * @return A String representing the time.
   */
  public static String timestampToString(Timestamp time) {
    return DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm").format(time.toLocalDateTime());
  }
}
