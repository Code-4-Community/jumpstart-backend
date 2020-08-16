package com.codeforcommunity.database.records;

/**
 * This is a DTO for the database. We want to keep stuff from the api module separate from what's in
 * the persist module.
 */
public class PostRecord {
  private Integer id;
  private String author;
  private String dateCreated;
  private String title;
  private Integer clapCount;
  private String body;
  private Integer commentCount;

  public PostRecord(
      Integer id,
      String author,
      String dateCreated,
      String title,
      Integer clapCount,
      String body,
      Integer commentCount) {
    this.id = id;
    this.author = author;
    this.dateCreated = dateCreated;
    this.title = title;
    this.clapCount = clapCount;
    this.body = body;
    this.commentCount = commentCount;
  }

  public Integer getId() {
    return this.id;
  }

  public String getAuthor() {
    return author;
  }

  public Integer getClapCount() {
    return clapCount;
  }

  public String getBody() {
    return body;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public String getTitle() {
    return title;
  }

  public Integer getCommentCount() {
    return commentCount;
  }
}
