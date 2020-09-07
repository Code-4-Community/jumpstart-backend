package com.codeforcommunity.dto.response;

/** A DTO (see {@link SinglePostResponse}) for a comment on a post. */
public class Comment {
  private Integer id;
  private Integer postId;
  private String dateCreated;
  private String author;
  private Integer clapCount;
  private String body;

  public Comment(
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
}
