package com.codeforcommunity.rest;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/** A helper interface for providing some functions for REST operations. */
public interface RestFunctions {

  /**
   * Gets a specific route parameter as an int. This calls {@link
   * #getRequestParameterAsString(HttpServerRequest, String)} and attempts to convert the string
   * value to an integer. In a path like {@code /posts/:post_id/comments}, this allows you to get
   * the value of {@code :post_id} as a number.
   *
   * @param req The request object. Can be retrieved using {@code ctx.request()}.
   * @param name The name of the parameter.
   * @return An integer representing the value. Will throw an exception if the value is null or no
   *     value exists.
   */
  static int getRequestParameterAsInt(HttpServerRequest req, String name) {
    // Get the parameter value as a string.
    String paramValue = getRequestParameterAsString(req, name);
    try {
      // Try to parse the value as an int.
      return Integer.parseInt(paramValue);
    } catch (NumberFormatException ex) {
      // Throw an exception if the value is not an int.
      throw new IllegalArgumentException(
          "Request parameter " + name + " could not be converted " + "to int.");
    }
  }

  /**
   * Gets a specific route parameter as a string. In a path like {@code /posts/:post_id/comments},
   * this allows you to get the value of {@code :post_id} as a string.
   *
   * @param req The request object. Can be retrieved using {@code ctx.request()}.
   * @param name The name of the parameter.
   * @return A string representing the value. Will throw an exception if the value is null or no
   *     value exists.
   */
  static String getRequestParameterAsString(HttpServerRequest req, String name) {
    // Get the specific param value as a string.
    String paramValue = req.getParam(name);
    // Return the value if the it is non-null and present.
    if (paramValue != null && !paramValue.isEmpty()) {
      return paramValue;
    }
    // Throw an exception if the value is null or empty.
    throw new IllegalArgumentException("Request parameter " + name + " Could not be found.");
  }

  /**
   * Gets a query parameter that may or may not be there as an optional of the desired type.
   * Attempts to map the query parameter from a string to an instance of the desired type. Returns
   * an {@link Optional} which may or may not contain a value. In a path like
   * {@code /posts/:post_id/comments?someValue=true}, this allows you to get the value(s) of
   * {@code someValue}, and convert it to a different type using {@code mapper}, and then return it
   * as an Optional.
   *
   * @param ctx The routing context to retrieve query param from.
   * @param name The name of the query param.
   * @param mapper A function that maps the query params from string to desired type. It accepts a
   *     List<String> and can return anything.
   * @param <T> The desired type.
   * @return An optional object of the query param as it's desired type.
   */
  static <T> Optional<T> getOptionalQueryParam(
      RoutingContext ctx, String name, Function<List<String>, T> mapper) {
    // Gets a list of strings for the possible query parameter.
    List<String> params = ctx.queryParam(name);
    // Declares the return value as an object of type T.
    T returnValue;
    // If value is present, attempt to map. Otherwise, just set the value to null.
    if (!params.isEmpty()) {
      try {
        // Try to map the object(s).
        returnValue = mapper.apply(params);
      } catch (Throwable t) {
        // Throw an IllegalArgumentException if anything is thrown. Note: Throwable is the
        // superclass of all Errors and Exception.
        throw new IllegalArgumentException("Issue found while mapping param", t);
      }
    } else {
      returnValue = null;
    }
    // Return an Optional wrapped possibly null value. An Optional is a wrapper for possibly null
    // values which provides a lot of helpful methods for dealing with null values.
    return Optional.ofNullable(returnValue);
  }
}
