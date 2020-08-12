package com.codeforcommunity.dto.response;

import java.util.List;

/** A DTO (see {@link SinglePostResponse}) for a list of comments. */
public class CommentsResponse {
  public List<Comment> comments;

  public CommentsResponse(List<Comment> comments) {
    // Create a new list with the same exact comment objects. We want to do this so that if the
    // original list is modified, our copy will stay the same.
    this.comments = List.copyOf(comments);
  }

  public List<Comment> getComments() {
    return comments;
  }
}
