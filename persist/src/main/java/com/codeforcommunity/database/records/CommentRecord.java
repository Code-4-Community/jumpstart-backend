package com.codeforcommunity.database.records;

/**
 * This is a DTO for the database. We want to keep stuff from the api module separate from what's in
 * the persist module.
 */
public class CommentRecord {
  private Integer id;
  private Integer postId;
  private String dateCreated;
  private String author;
  private Integer clapCount;
  private String body;

  public CommentRecord(
      Integer id,
      Integer postId,
      String author,
      String body,
      String dateCreated,
      Integer clapCount) {
    this.id = id;
    this.postId = postId;
    this.author = author;
    this.body = body;
    this.dateCreated = dateCreated;
    this.clapCount = clapCount;
  }

  public CommentRecord(Integer postId, String author, String body) {
    this.id = null;
    this.postId = postId;
    this.author = author;
    this.body = body;
    this.dateCreated = null;
    this.clapCount = null;
  }

  public Integer getId() {
    return this.id;
  }

  public Integer getPostId() {
    return postId;
  }

  public String getDateCreated() {
    return dateCreated;
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

  public void setId(Integer id) {
    this.id = id;
  }

  public void setDateCreated(String dateCreated) {
    this.dateCreated = dateCreated;
  }

  public void setClapCount(Integer clapCount) {
    this.clapCount = clapCount;
  }
}
