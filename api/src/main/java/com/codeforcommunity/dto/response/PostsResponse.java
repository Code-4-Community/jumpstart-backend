package com.codeforcommunity.dto.response;

import java.util.List;

/** Another DTO (see {@link SinglePostResponse}) which contains a list of individual posts. */
public class PostsResponse {
  private List<PostSummary> posts;

  public PostsResponse(List<PostSummary> posts) {
    // Create a new list with the same exact comment objects. We want to do this so that if the
    // original list is modified, our copy will stay the same.
    this.posts = List.copyOf(posts);
  }

  public List<PostSummary> getPosts() {
    return posts;
  }
}
