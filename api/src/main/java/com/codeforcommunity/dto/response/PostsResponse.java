package com.codeforcommunity.dto.response;

import java.util.List;

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
