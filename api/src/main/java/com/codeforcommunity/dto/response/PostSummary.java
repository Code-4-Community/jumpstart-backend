package com.codeforcommunity.dto.response;

public class PostSummary {
  private Integer id;
  private String author;
  private String dateCreated;
  private String title;
  private Integer clapCount;
  private String preview;

  public PostSummary(
      Integer id,
      String author,
      String dateCreated,
      String title,
      Integer clapCount,
      String preview) {
    this.id = id;
    this.author = author;
    this.dateCreated = dateCreated;
    this.title = title;
    this.clapCount = clapCount;
    this.preview = preview;
  }

  public PostSummary(SinglePostResponse post, String preview) {
    this.id = post.getId();
    this.author = post.getAuthor();
    this.dateCreated = post.getDateCreated();
    this.title = post.getTitle();
    this.clapCount = post.getClapCount();
    this.preview = preview;
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
}
