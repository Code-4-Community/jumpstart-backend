package com.codeforcommunity.dto.request;

/** A DTO representing a request to create a comment. */
public class CreateCommentRequest {
  private String author;
  private String body;

  /**
   * This private constructor is used by Vertx's {@link io.vertx.core.json.JsonObject} mapper to
   * create an instance of this class and then unload each field of the JSON into this class. It's
   * done using reflection, which is why the regular constructor we use isn't sufficient.
   */
  private CreateCommentRequest() {}

  public CreateCommentRequest(String author, String body) {
    this.author = author;
    this.body = body;
  }

  public String getAuthor() {
    return author;
  }

  public String getBody() {
    return body;
  }

  public boolean validate() {
    return author != null && body != null;
  }
}
