package com.codeforcommunity.api;

import com.codeforcommunity.dto.request.CreatePostRequest;
import com.codeforcommunity.dto.response.CommentsResponse;
import com.codeforcommunity.dto.response.PostsResponse;
import com.codeforcommunity.dto.response.SinglePostResponse;

/**
 * Represents an object which can Process blog post requests. We have this interface declared in the
 * {@code api} module (which is separate from this interface's immediate parent '../api' directory)
 * so that we can access the methods an {@code IPostsProcessor} would use in this module. Remember
 * that there is no dependency on the {@code service} module in the {@code api} module, so an
 * interface is used instead of importing the {@code
 * com.codeforcommunity.processors.PostsProcessor}.
 */
public interface IPostsProcessor {

  /**
   * Returns all posts for the front page.
   *
   * @return A PostsResponse object containing a list of {@link SinglePostResponse}.
   */
  PostsResponse getPosts();

  /**
   * Returns a specific post. Accessed at the "/posts/:post_id" route.
   *
   * @param postId The ID of the post to return.
   * @return The individual post.
   */
  SinglePostResponse getSinglePost(int postId);

  /**
   * Returns a list of comments for a specific post. Accessed at the "/posts/:post_id/comments"
   * route.
   *
   * @param postId The ID of the post to return.
   * @return The list of comments.
   */
  CommentsResponse getCommentsForPost(int postId);

  /**
   * Save the provided post to the database.
   *
   * @param post The post to save.
   */
  void createPost(CreatePostRequest post);
}
