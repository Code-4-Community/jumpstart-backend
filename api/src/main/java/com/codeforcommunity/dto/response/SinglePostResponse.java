package com.codeforcommunity.dto.response;

public class SinglePostResponse {
  private Integer id;
  private String author;
  private String dateCreated;
  private String title;
  private Integer clapCount;
  private String body;

  public SinglePostResponse(
      Integer id, String author, String dateCreated, String title, Integer clapCount, String body) {
    this.id = id;
    this.author = author;
    this.dateCreated = dateCreated;
    this.title = title;
    this.clapCount = clapCount;
    this.body = body;
  }

  public Integer getId() {
    return this.id;
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

  public String getBody() {
    return body;
  }
}
