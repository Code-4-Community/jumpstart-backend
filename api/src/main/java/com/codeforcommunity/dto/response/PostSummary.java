package com.codeforcommunity.dto.response;

/**
 * This class is a DTO (see {@link SinglePostResponse}) for a summary of a post. This has a preview
 * instead of a body.
 */
public class PostSummary {
  private Integer id;
  private String author;
  private String dateCreated;
  private String title;
  private Integer clapCount;
  private String preview;
  private Integer commentCount;

  public PostSummary(
      Integer id,
      String author,
      String dateCreated,
      String title,
      Integer clapCount,
      String preview,
      Integer commentCount) {
    this.id = id;
    this.author = author;
    this.dateCreated = dateCreated;
    this.title = title;
    this.clapCount = clapCount;
    this.preview = preview;
    this.commentCount = commentCount;
  }

  public PostSummary(SinglePostResponse post, String preview, Integer commentCount) {
    this.id = post.getId();
    this.author = post.getAuthor();
    this.dateCreated = post.getDateCreated();
    this.title = post.getTitle();
    this.clapCount = post.getClapCount();
    this.preview = preview;
    this.commentCount = commentCount;
  }

  public Integer getId() {
    return id;
  }

  public String getAuthor() {
    return author;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public String getTitle() {
    return title;
  }

  public Integer getClapCount() {
    return clapCount;
  }

  public String getPreview() {
    return preview;
  }

  public Integer getCommentCount() {
    return commentCount;
  }
}
