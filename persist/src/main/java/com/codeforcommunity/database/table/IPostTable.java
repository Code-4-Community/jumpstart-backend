package com.codeforcommunity.database.table;

import com.codeforcommunity.database.records.PostRecord;
import java.util.List;

/**
 * Our interface for a table containing {@link PostRecord}s. For a while, we'll have an in-memory
 * implementation, which will eventually be replaced by a real database. We're doing this in its own
 * interface because when we switch it out for the real one, we want it to be as smooth and seamless
 * as possible.
 */
public interface IPostTable {

  /**
   * Get a {@link PostRecord} by a provided id.
   *
   * @param id The ID of the post we want to return.
   * @return The post with the given ID.
   */
  PostRecord getById(int id);

  /**
   * Get all {@link PostRecord}s.
   *
   * @return A list containing all posts.
   */
  List<PostRecord> getAllPosts();

  /**
   * Save the given post to our database.
   *
   * @param post The post to be saved.
   */
  void savePost(PostRecord post);

  /**
   * Determine if the given id belongs to an existing post.
   *
   * @param postId The ID of the post to check.
   * @return True if the post exists, false otherwise.
   */
  boolean postExists(int postId);

  /**
   * Increment the clap count for the given post. Assumes the post with the given ID exists.
   *
   * @param postId The ID of the post to clap.
   */
  void clapPost(int postId);

  /**
   * Delete the post by the given ID.
   *
   * @param postId The ID of the post to delete.
   */
  void deletePost(int postId);
}
