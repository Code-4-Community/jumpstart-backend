package com.codeforcommunity.mapper;

import com.codeforcommunity.database.records.CommentRecord;
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
}
