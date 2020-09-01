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
   * Converts a selection using the {@code SELECT *} statement to a {@link PostRecord}.
   *
   * @param res The current {@link ResultSet} row to pull data from.
   * @return A PostRecord containing the row's data.
   * @throws SQLException If there is an issue getting data from the row. This could be because
   *     there is no current row, you're trying to get data from a column that doesn't exist, you're
   *     trying to convert to the wrong type, or other reasons.
   */
  private static PostRecord allFieldsResultSetToRecord(ResultSet res) throws SQLException {
    // Convert the timestamp to a human-readable string.
    String time = timestampToString(res.getTimestamp("date_created"));
    // Return a new PostRecord with the found data. You can either get the column by the index
    // (so if we said 'SELECT id, author, ...', id would be index 1, author 2, ...) or by column
    // name.
    return new PostRecord(
        res.getInt("id"),
        res.getString("author"),
        time,
        res.getString("title"),
        // Null is returned for clap_count if there is no post_claps record during the join. The
        // getInt method turns null into a 0, since 'int' data types cannot be null.
        res.getInt("clap_count"),
        res.getString("body"));
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
      // We're using a LEFT JOIN instead of just a JOIN here since we want post information to be
      // returned even if clap information doesn't exist.
      String sql =
          "SELECT posts.*, clap_count "
              + "FROM posts "
              + "LEFT JOIN (SELECT post_id, COUNT(*) AS clap_count FROM post_claps GROUP BY post_id) claps "
              + "ON posts.id = claps.post_id "
              + "WHERE posts.id = ?;";
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
      String sql =
          "SELECT posts.*, clap_count "
              + "FROM posts "
              + "LEFT JOIN (SELECT post_id, COUNT(*) AS clap_count FROM post_claps GROUP BY post_id) claps "
              + "ON posts.id = claps.post_id;";
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
    boolean commentExists = false;
    try {
      Connection conn = getConnection();
      // In this case, we don't want to select all fields because getting a larger number
      // of fields is a slower operation.
      String sql =
          "SELECT id "
              + "FROM posts "
              + "LEFT JOIN (SELECT post_id, COUNT(*) AS clap_count FROM post_claps GROUP BY post_id) claps "
              + "ON posts.id = claps.post_id "
              + "WHERE id = ?;";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, postId);
      ResultSet res = stmt.executeQuery();
      if (res.next()) {
        commentExists = true;
      }

      res.close();
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
    return commentExists;
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
    try {
      Connection conn = getConnection();
      // Here, we're updating the rows in the post table by the given id
      // by incrementing the clap count.
      String sql = "INSERT INTO post_claps (post_id) VALUES (?);";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, postId);
      stmt.execute();

      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
  }

  @Override
  public void deletePost(int postId) {
    try {
      Connection conn = getConnection();
      String sql = "DELETE FROM posts WHERE id = ?;";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, postId);
      stmt.execute();

      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
  }
}
