package com.codeforcommunity.dto.response;

import java.util.List;

/** Another DTO (see {@link SinglePostResponse}) which contains a list of individual posts. */
public class PostsResponse {
  private List<PostSummary> posts;

  private PostsResponse() {}

  public PostsResponse(List<PostSummary> posts) {
    this.posts = List.copyOf(posts);
  }

  public List<PostSummary> getPosts() {
    return posts;
  }
}
