package com.codeforcommunity.rest;

import static com.codeforcommunity.rest.IRouter.end;
import static com.codeforcommunity.rest.RequestUtils.getRequestParameterAsInt;

import com.codeforcommunity.api.IPostsProcessor;
import com.codeforcommunity.dto.response.PostsResponse;
import com.codeforcommunity.dto.response.SinglePostResponse;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class PostsRouter implements IRouter {
  private IPostsProcessor processor;

  public PostsRouter(IPostsProcessor postsProcessor) {
    this.processor = postsProcessor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    // Set a router object
    Router router = Router.router(vertx);

    // Call a helper method to register this router's routes.
    this.registerRoutes(router);

    return router;
  }

  /**
   * Register this {@link IRouter}'s routes with the {@link Router} object.
   *
   * @param router The Router to register the routes with.
   */
  private void registerRoutes(Router router) {
    this.registerGetPostsRoute(router);
    this.registerGetSinglePostRoute(router);
  }

  /**
   * Get the "/posts" route. We already initialized this sub-router with the "/posts" path prefix,
   * so we only need to register this as a "/" (root) route here to set it up. This method only gets
   * called once when setting up the router.
   *
   * @param router The Router to register the route with.
   */
  private void registerGetPostsRoute(Router router) {
    // Create a Route on the Router for the "/" (root) route. Remember that we're in a sub-router
    // mounted at "/posts".
    Route route = router.get("/");

    // Add a handler method reference (a type of lambda) to the route. This registerGetPostsRoute
    // method only gets called once to set up the handler, but the handleGetPostsRoute method
    // gets called every time the route is accessed.
    route.handler(this::handleGetPostsRoute);
  }

  /**
   * Handle the "/posts" route. This gets called when someone accesses the route. It is set up once
   * in {@link #registerGetPostsRoute(Router)}, and then called many times after that.
   *
   * @param ctx The {@link RoutingContext} containing all relevant routing info.
   */
  private void handleGetPostsRoute(RoutingContext ctx) {
    // Get all posts using the provided processor.
    PostsResponse response = this.processor.getPosts();

    // Call our helper method to end the request, and provide a success status code with
    // our response.
    end(ctx.response(), 200, JsonObject.mapFrom(response).encode());
  }

  /**
   * Get the "/posts/:post_id" route. The ":post_id" part allows us to access a value from that part
   * of the route. In this case, if someone were to access "/posts/256", we can get the "256" as a
   * value and use it to return a response.
   *
   * @param router The Router to register the route with.
   */
  private void registerGetSinglePostRoute(Router router) {
    // Create a Route on the Router for the "/:post_id" route.
    Route route = router.get("/:post_id");
    route.handler(this::handleGetSinglePostRoute);
  }

  /**
   * Handle the "/posts/:post_id" route.
   *
   * @param ctx The {@link RoutingContext} containing all relevant routing info.
   */
  private void handleGetSinglePostRoute(RoutingContext ctx) {
    // Call a helper method to get the "post_id" route param from the routing context.
    int postId = getRequestParameterAsInt(ctx.request(), "post_id");

    // Get the specific post from the processor using the provided postId.
    SinglePostResponse response = this.processor.getSinglePost(postId);

    // Return a 404 NOT FOUND if post does not exist.
    if (response == null) {
      end(ctx.response(), 404, "Post with ID " + postId + " not found");
    }
    // Otherwise, return the found object.
    else {
      end(ctx.response(), 200, JsonObject.mapFrom(response).encode());
    }
  }
}
