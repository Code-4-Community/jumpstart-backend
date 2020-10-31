package com.codeforcommunity.mapper;

import com.codeforcommunity.database.records.CommentRecord;
import com.codeforcommunity.dto.request.CreateCommentRequest;
import com.codeforcommunity.dto.response.Comment;

/** An interface for mapping between the persist and api module DTOs. */
public class CommentMapper {

  /**
   * Map from a {@link CommentRecord} to a {@link Comment}.
   *
   * @param record The record to map.
   * @return The mapped DTO.
   */
  public static Comment recordToComment(CommentRecord record) {
    return new Comment(
        record.getId(),
        record.getPostId(),
        record.getAuthor(),
        record.getBody(),
        record.getDateCreated(),
        record.getClapCount());
  }

  /**
   * Map from a {@link CreateCommentRequest} to a {@link CommentRecord}.
   *
   * @param postId The ID of the post which the comment belongs to.
   * @param request The request to map.
   * @return The mapped record.
   */
  public static CommentRecord createRequestToRecord(int postId, CreateCommentRequest request) {
    return new CommentRecord(postId, request.getAuthor(), request.getBody());
  }
}
