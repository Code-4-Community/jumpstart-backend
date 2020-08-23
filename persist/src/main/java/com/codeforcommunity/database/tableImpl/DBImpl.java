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
   * A method which can be used by the {@link PostTableDBImpl} and {@link CommentTableDBImpl} to
   * determine if an item exists since the method bodies will be very similar.
   *
   * @param itemId The ID of the item to search for.
   * @param table The name of the table in the database.
   * @return True if the item exists.
   */
  protected boolean itemExists(int itemId, String table) {
    boolean itemExists = false;
    try {
      Connection conn = getConnection();
      // In this case, we don't want to select all fields because getting a larger number
      // of fields is a slower operation.
      String sql = "SELECT id FROM ? WHERE id = ?;";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, table);
      stmt.setInt(2, itemId);
      ResultSet res = stmt.executeQuery();
      if (res.next()) {
        itemExists = true;
      }

      res.close();
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
    return itemExists;
  }

  /**
   * A method which can be used by the {@link PostTableDBImpl} and {@link CommentTableDBImpl} to
   * clap an item since the method bodies will be very similar.
   *
   * @param itemId The ID of the item to search for.
   * @param table The name of the table in the database.
   */
  protected void clapItem(int itemId, String table) {
    try {
      Connection conn = getConnection();
      // Here, we're updating the rows in the post table by the given id
      // by incrementing the clap count.
      String sql = "UPDATE ? SET clap_count = clap_count + 1 WHERE id = ?;";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, table);
      stmt.setInt(2, itemId);
      stmt.execute();

      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
  }

  /**
   * A method which can be used by the {@link PostTableDBImpl} and {@link CommentTableDBImpl} to
   * delete an item since the method bodies will be very similar.
   *
   * @param itemId The ID of the item to search for.
   * @param table The name of the table in the database.
   */
  protected void deleteItem(int itemId, String table) {
    try {
      Connection conn = getConnection();
      String sql = "DELETE FROM ? WHERE id = ?;";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, table);
      stmt.setInt(2, itemId);
      stmt.execute();

      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
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
