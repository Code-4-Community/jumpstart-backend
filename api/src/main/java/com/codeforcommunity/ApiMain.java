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

/**
 * Our 'ServiceMain' for initializing the API server. Sets up routing and actually sets the server
 * to listen on some port (initially 8081).
 */
public class ApiMain {
  public static final int defaultPort = 8081;

  public ApiMain() {}

  /** The initialize the sub-router and start the API server. */
  public void startApi() {
    // Get a 'Vertx' object. This will provide us an HttpServer and Router.
    Vertx vertx = Vertx.vertx();
    // Get an HttpServer from the Vertx object.
    HttpServer server = vertx.createHttpServer();

    // Create a new router using the vertx object.
    Router router = Router.router(vertx);
    /*
     * Set up CORS so that back end and front end can interact through different servers. Usually
     * web browsers will only allow a front end to fetch resources from back-ends
     * that have the same address as them (with a few caveats), but this allows us to 'allow' other
     * things too.
     * See https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS for more info.
     */
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
    /*
     * Set the handler method for the home path. Whenever the home path is loaded, the 'handleHome'
     * function is called.
     *
     * The "this::handleHome" style is called a method reference, and it is a type of lambda in
     * Java. For more info on lambdas in Java check out the following:
     * - Lambdas https://www.tutorialspoint.com/java8/java8_lambda_expressions.htm
     * - Method References https://www.tutorialspoint.com/java8/java8_method_references.htm
     * - Functional Interfaces https://www.tutorialspoint.com/java8/java8_functional_interfaces.htm
     */

    home.handler(this::handleHome);

    // Start the server and listen on port :8081
    // (you can access this locally at http://localhost:8081)
    server.requestHandler(router).listen(defaultPort);

    // Let the user know the server has started
    System.out.println("Hey! The server has started on port " + defaultPort);
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
            // Sets the CORS to the same thing we did above above for the response
            .putHeader("Access-Control-Allow-Origin", "*")
            .putHeader("Access-Control-Allow-Methods", "DELETE, POST, GET, OPTIONS")
            .putHeader(
                "Access-Control-Allow-Headers",
                "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

    // Returns 'Hello Jumpstarters!' as a response
    response.end("Hello Jumpstarters!");
  }
}
