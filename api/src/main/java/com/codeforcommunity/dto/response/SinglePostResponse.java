package com.codeforcommunity.dto.response;

/**
 * This class represents an individual post. It is a DTO (Data Transfer Object), and a DTO's purpose
 * is only to do the bare minimum to store data and provide access to it. In most cases, the only
 * methods available on a DTO are to get and (sometimes) set data, and usually nothing else.
 */
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
}
