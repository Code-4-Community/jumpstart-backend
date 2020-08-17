package com.codeforcommunity.database.table;

import com.codeforcommunity.database.records.CommentRecord;
import com.codeforcommunity.database.records.PostRecord;
import java.util.List;

/**
 * Our interface for a table containing {@link CommentRecord}s. For a while, we'll have an in-memory
 * implementation, which will eventually be replaced by a real database. We're doing this in its own
 * interface because when we switch it out for the real one, we want it to be as smooth and seamless
 * as possible.
 */
public interface ICommentTable {

  /**
   * Get a list of comments pertaining to the given {@link PostRecord} by ID. Returns an empty list
   * if none exist.
   *
   * @param postId The ID of the post the comments belong to.
   * @return The list of comments related to that post.
   */
  List<CommentRecord> getByPostId(int postId);

  /**
   * Save the given comment to the database.
   *
   * @param comment The comment to save.
   */
  void saveComment(CommentRecord comment);

  /**
   * Determine if the given id belongs to an existing comment.
   *
   * @param postId The ID the comment belongs to.
   * @param commentId The ID of the comment to check.
   * @return True if the comment exists, false otherwise.
   */
  boolean commentExists(int postId, int commentId);

  /**
   * Clap the given comment with the provided IDs if it exists. This method assumes the comment
   * exists.
   *
   * @param postId The ID of the post the comment belongs to.
   * @param commentId The ID of the comment to clap.
   */
  void clapComment(int postId, int commentId);

  /**
   * Delete all of the comments belonging to the given post.
   *
   * @param postId The ID of the post to delete comments for.
   */
  void deleteCommentsByPostId(int postId);

  /**
   * Delete the comment related to the given IDs. This method assumes that the post exists and has
   * comments.
   *
   * @param postId The ID of the post the comment belongs to.
   * @param commentId The ID of the comment to delete.
   */
  void deleteComment(int postId, int commentId);
}
