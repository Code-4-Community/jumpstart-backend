package com.codeforcommunity.database.tableImpl;

import com.codeforcommunity.database.records.PostRecord;
import com.codeforcommunity.database.table.IPostTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PostTableDBImpl extends DBImpl implements IPostTable {
  private static final String ALL_POST_FIELDS = "id, author, date_created, title, clap_count, body";

  /**
   * The constructor which just calls the {@link DBImpl} super constructor.
   *
   * @param dbProperties A {@link Properties} we expect to contain the values for url, user, and
   *     password for connecting to the database.
   */
  public PostTableDBImpl(Properties dbProperties) {
    super(dbProperties);
  }

  /**
   * Converts a selection using the {@code ALL_POST_FIELDS} select statement to a {@link
   * PostRecord}.
   *
   * @param res The current {@link ResultSet} row to pull data from.
   * @return A PostRecord containing the row's data.
   * @throws SQLException If there is an issue getting data from the row. This could be because
   *     there is no current row, you're trying to get data from a columnIndex that doesn't exist,
   *     you're trying to convert to the wrong type, or other reasons.
   */
  private PostRecord allFieldsResultSetToRecord(ResultSet res) throws SQLException {
    // Convert the timestamp to a human-readable string.
    String time = timestampToString(res.getTimestamp(3));
    // Return a new PostRecord with the found data
    // Returning comment count as 0 until advanced database
    return new PostRecord(
        res.getInt(1),
        res.getString(2),
        time,
        res.getString(4),
        res.getInt(5),
        res.getString(6),
        0);
  }

  @Override
  public PostRecord getById(int id) {
    PostRecord record = null;
    try {
      // Get our database connection.
      Connection conn = getConnection();
      // Create our SQL string. This one gets all of the fields of a Post by a given ID.
      // The '?' allows us to safely insert that variable into the query without having to worry
      // about escaping any special characters inside.
      String sql = "SELECT " + ALL_POST_FIELDS + " FROM posts " + "WHERE id = ?;";
      // A PreparedStatement is the technique that allows us to insert variables by '?'.
      PreparedStatement stmt = conn.prepareStatement(sql);
      // Set the first '?' = id. Note how in prepared statements, parameters are not 0-indexed.
      stmt.setInt(1, id);

      // Get our results. This could also be done in two separate calls;
      // stmt.execute() and stmt.getResultSet().
      ResultSet res = stmt.executeQuery();
      // The next row in the table is queued up by calling ResultSet.next(). If ResultSet.next()
      // returns false, then there are no more rows (or no rows were found if
      // this is the first call).
      if (res.next()) {
        record = allFieldsResultSetToRecord(res);
      }

      res.close();
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
    if (record == null) {
      throw new IllegalArgumentException("No post with ID " + id + " exists.");
    }
    return record;
  }

  @Override
  public List<PostRecord> getAllPosts() {
    List<PostRecord> posts = new ArrayList<>();
    try {
      Connection conn = getConnection();
      String sql = "SELECT " + ALL_POST_FIELDS + " FROM posts;";
      PreparedStatement stmt = conn.prepareStatement(sql);
      ResultSet res = stmt.executeQuery();

      // Since ResultSet.next() queues up the next row and lets you know if there are any left,
      // we just iterate through the found records like this.
      while (res.next()) {
        posts.add(allFieldsResultSetToRecord(res));
      }

      res.close();
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }

    return posts;
  }

  @Override
  public boolean postExists(int postId) {
    return this.itemExists(postId, "posts");
  }

  @Override
  public void savePost(PostRecord post) {
    try {
      Connection conn = getConnection();
      // We're setting ONLY the author, title, and body since the database will provide for us the
      // id, date_created, and clap_count automatically.
      String sql = "INSERT INTO posts (author, title, body) VALUES (?, ?, ?);";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, post.getAuthor());
      stmt.setString(2, post.getTitle());
      stmt.setString(3, post.getBody());

      stmt.execute();

      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
  }

  @Override
  public void clapPost(int postId) {
    this.clapItem(postId, "posts");
  }

  @Override
  public void deletePost(int postId) {
    this.deleteItem(postId, "posts");
  }
}
