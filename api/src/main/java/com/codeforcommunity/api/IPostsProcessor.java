package com.codeforcommunity.api;

import com.codeforcommunity.dto.response.PostsResponse;
import com.codeforcommunity.dto.response.SinglePostResponse;

/** Represents an object which can Process blog post requests. */
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
}
