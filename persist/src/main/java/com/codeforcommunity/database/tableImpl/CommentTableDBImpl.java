package com.codeforcommunity.database.tableImpl;

import com.codeforcommunity.database.records.CommentRecord;
import com.codeforcommunity.database.table.ICommentTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CommentTableDBImpl extends DBImpl implements ICommentTable {
  /**
   * The constructor which just calls the {@link DBImpl} super constructor.
   *
   * @param dbProperties A {@link Properties} we expect to contain the values for url, user, and
   *     password for connecting to the database.
   */
  public CommentTableDBImpl(Properties dbProperties) {
    super(dbProperties);
  }

  /**
   * Converts a selection using the {@code SELECT *} statement to a {@link CommentRecord}.
   *
   * @param res The current {@link ResultSet} row to pull data from.
   * @return A CommentRecord containing the row's data.
   * @throws SQLException If there is an issue getting data from the row. This could be because
   *     there is no current row, you're trying to get data from a column that doesn't exist, you're
   *     trying to convert to the wrong type, or other reasons.
   */
  private static CommentRecord allFieldsResultSetToRecord(ResultSet res) throws SQLException {
    // Convert the timestamp to a human-readable string.
    String time = timestampToString(res.getTimestamp("date_created"));
    // Return a new PostRecord with the found data. You can either get the column by the index
    // (so if we said 'SELECT id, author, ...', id would be index 1, author 2, ...) or by column
    // name.
    return new CommentRecord(
        res.getInt("id"),
        res.getInt("post_id"),
        res.getString("author"),
        res.getString("body"),
        time,
        res.getInt("clap_count"));
  }

  @Override
  public List<CommentRecord> getByPostId(int postId) {
    List<CommentRecord> comments = new ArrayList<>();
    try {
      // Get our database connection.
      Connection conn = getConnection();
      // Create our SQL string. This one gets all of the fields of a Post by a given ID.
      // The '?' allows us to safely insert that variable into the query without having to worry
      // about escaping any special characters inside.
      String sql = "SELECT * FROM comments WHERE post_id = ?;";
      // A PreparedStatement is the technique that allows us to insert variables by '?'.
      PreparedStatement stmt = conn.prepareStatement(sql);
      // Set the first '?' = id. Note how in prepared statements, parameters are not 0-indexed.
      stmt.setInt(1, postId);

      // Get our results. This could also be done in two separate calls;
      // stmt.execute() and stmt.getResultSet().
      ResultSet res = stmt.executeQuery();
      // The next row in the table is queued up by calling ResultSet.next(). If ResultSet.next()
      // returns false, then there are no more rows (or no rows were found if
      // this is the first call).
      while (res.next()) {
        comments.add(allFieldsResultSetToRecord(res));
      }

      res.close();
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
    return comments;
  }

  @Override
  public void saveComment(CommentRecord comment) {
    try {
      Connection conn = getConnection();
      String sql = "INSERT INTO comments (post_id, author, body) VALUES (?, ?, ?);";

      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, comment.getPostId());
      stmt.setString(2, comment.getAuthor());
      stmt.setString(3, comment.getBody());

      stmt.execute();

      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
  }

  @Override
  public boolean commentExists(int postId, int commentId) {
    boolean commentExists = false;
    try {
      Connection conn = getConnection();
      String sql = "SELECT id FROM comments WHERE post_id = ? AND id = ?;";

      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, postId);
      stmt.setInt(2, commentId);

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
  public void clapComment(int postId, int commentId) {
    try {
      Connection conn = getConnection();
      // Here, we're updating the rows in the post table by the given id
      // by incrementing the clap count.
      String sql = "UPDATE comments SET clap_count = clap_count + 1 WHERE post_id = ? AND id = ?;";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, postId);
      stmt.setInt(2, commentId);
      stmt.execute();

      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
  }

  @Override
  public void deleteCommentsByPostId(int postId) {
    try {
      Connection conn = getConnection();
      String sql = "DELETE FROM comments WHERE post_id = ?;";
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
  public void deleteComment(int postId, int commentId) {
    try {
      Connection conn = getConnection();
      String sql = "DELETE FROM comments WHERE post_id = ? AND id = ?;";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, postId);
      stmt.setInt(2, commentId);
      stmt.execute();

      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
  }

  @Override
  public int getCommentCountForPost(int postId) {
    int count = 0;
    try {
      Connection conn = getConnection();
      String sql = "SELECT COUNT(*) FROM comments WHERE post_id = ?;";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, postId);

      ResultSet res = stmt.executeQuery();
      if (res.next()) {
        count = res.getInt(1);
      } else {
        throw new IllegalArgumentException("No post with id " + postId + " exists.");
      }

      res.close();
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      throw new IllegalStateException("There was an issue interacting with the database.", e);
    }
    return count;
  }
}
