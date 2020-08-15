package com.codeforcommunity.rest;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

/**
 * An interface for any router-like objects. Provides the {@link #initializeRouter(Vertx)} stub
 * method and a few convenience 'end' methods.
 */
public interface IRouter {

  /**
   * Initializes the router using the provided {@link Vertx} object.
   *
   * @param vertx The Vertx object created by {@code com.codeforcommunity.ServiceMain}
   * @return A set up {@link Router}.
   */
  Router initializeRouter(Vertx vertx);

  /**
   * Ends the request by calling the {@link #end(HttpServerResponse, int, String, String)} method.
   * This method returns no response body.
   *
   * @param response The response object used to initiate the response.
   * @param statusCode The status code.
   */
  static void end(HttpServerResponse response, int statusCode) {
    end(response, statusCode, null);
  }

  /**
   * Ends the request by calling the {@link #end(HttpServerResponse, int, String, String)} method.
   * This method returns a JSON response body.
   *
   * @param response The response object used to initiate the response.
   * @param statusCode The status code.
   * @param jsonBody The body to return.
   */
  static void end(HttpServerResponse response, int statusCode, String jsonBody) {
    end(response, statusCode, jsonBody, "application/json");
  }

  /**
   * Ends the request by setting the response status code, content type, CORS values, and response
   * body.
   *
   * @param response The response object used to initiate the response.
   * @param statusCode The status code.
   * @param jsonBody The body to return.
   * @param contentType The Content-Type of the body.
   */
  static void end(
      HttpServerResponse response, int statusCode, String jsonBody, String contentType) {
    response
        .setStatusCode(statusCode)
        .putHeader("Content-Type", contentType)
        .putHeader("Access-Control-Allow-Origin", "*")
        .putHeader("Access-Control-Allow-Methods", "DELETE, POST, GET, OPTIONS")
        .putHeader(
            "Access-Control-Allow-Headers",
            "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
    if (jsonBody == null || jsonBody.equals("")) {
      response.end();
    } else {
      response.end(jsonBody);
    }
  }
}
