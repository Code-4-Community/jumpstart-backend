package com.codeforcommunity.mapper;

import com.codeforcommunity.database.records.PostRecord;
import com.codeforcommunity.dto.response.PostSummary;
import com.codeforcommunity.dto.response.SinglePostResponse;
import com.codeforcommunity.processor.PostsProcessor;

/** An interface for mapping between the persist and api module DTOs. */
public class PostMapper {

  /**
   * Map from a {@link PostRecord} to a {@link SinglePostResponse}.
   *
   * @param record The record to map.
   * @return The mapped DTO.
   */
  public static SinglePostResponse recordToResponse(PostRecord record) {
    return new SinglePostResponse(
        record.getId(),
        record.getAuthor(),
        record.getDateCreated(),
        record.getTitle(),
        record.getClapCount(),
        record.getBody());
  }

  /**
   * Map from a {@link PostRecord} to a {@link PostSummary}.
   *
   * @param record The record to map.
   * @return The mapped DTO.
   */
  public static PostSummary recordToSummary(PostRecord record) {
    return new PostSummary(
        record.getId(),
        record.getAuthor(),
        record.getDateCreated(),
        record.getTitle(),
        record.getClapCount(),
        PostsProcessor.getBodyPreview(record.getBody()),
        record.getCommentCount());
  }
}
