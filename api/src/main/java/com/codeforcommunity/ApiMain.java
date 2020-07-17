package com.codeforcommunity;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import java.util.Set;

public class ApiMain {
  public ApiMain() {}

  /** The initialize the sub-router and start the API server. */
  public void startApi() {
    Vertx vertx = Vertx.vertx();
    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);
    // Set up cors so that back end and front end can interact through different servers
    router
        .route()
        .handler(
            CorsHandler.create("*")
                .allowedMethods(
                    Set.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE))
                .allowedHeaders(
                    Set.of(
                        "Content-Type",
                        "origin",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Credentials",
                        "Access-Control-Allow-Headers",
                        "Access-Control-Request-Method")));

    // Create a route for the home (root) path
    Route home = router.route("/");
    home.handler(this::handleHome);

    // Start the server and listen on port :8081
    server.requestHandler(router).listen(8081);
  }

  /**
   * Handles evaluating the request provided in the {@link RoutingContext} and returning a response
   * for the home route.
   *
   * @param ctx An object containing request data provided by a Vertx route handler.
   */
  private void handleHome(RoutingContext ctx) {
    HttpServerResponse response =
        ctx.response()
            // Set the status code (200 = SUCCESS).
            .setStatusCode(200)
            // Sets the 'Content-Type' of this response
            .putHeader("Content-Type", "text/plain")
            // Sets the CORS values set up above for the response
            .putHeader("Access-Control-Allow-Origin", "*")
            .putHeader("Access-Control-Allow-Methods", "DELETE, POST, GET, OPTIONS")
            .putHeader(
                "Access-Control-Allow-Headers",
                "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

    // Returns 'Hello World' as a response
    response.end("Hello World");
  }
}
