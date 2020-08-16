package com.codeforcommunity.rest;

import static com.codeforcommunity.rest.IRouter.end;
import static com.codeforcommunity.rest.RequestUtils.getJsonBodyAsClass;
import static com.codeforcommunity.rest.RequestUtils.getRequestParameterAsInt;

import com.codeforcommunity.api.IPostsProcessor;
import com.codeforcommunity.dto.request.CreatePostRequest;
import com.codeforcommunity.dto.response.CommentsResponse;
import com.codeforcommunity.dto.response.PostsResponse;
import com.codeforcommunity.dto.response.SinglePostResponse;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class PostsRouter implements IRouter {
  /** Our processor! */
  private final IPostsProcessor processor;

  public PostsRouter(IPostsProcessor postsProcessor) {
    this.processor = postsProcessor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    // Set a router object
    Router router = Router.router(vertx);

    // Register this router's routes.
    this.registerGetPostsRoute(router);
    this.registerGetSinglePostRoute(router);
    this.registerGetCommentsForPostRoute(router);
    this.registerPostPostsRoute(router);

    return router;
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

    try {
      // Get the specific post from the processor using the provided postId.
      SinglePostResponse response = this.processor.getSinglePost(postId);
      // Return the found object.
      end(ctx.response(), 200, JsonObject.mapFrom(response).encode());
    } catch (IllegalArgumentException e) {
      // Return a 404 NOT FOUND if post does not exist.
      end(ctx.response(), 404, "Post with ID " + postId + " not found");
    }
  }

  /**
   * Register the "/posts/:post_id/comments" route.
   *
   * @param router The Router to register the route with.
   */
  private void registerGetCommentsForPostRoute(Router router) {
    // Create a Route on the Router for the "/:post_id/comments" route.
    Route route = router.get("/:post_id/comments");
    route.handler(this::handleGetCommentsForPost);
  }

  /**
   * Handle the "/posts/:post_id/comments" route.
   *
   * @param ctx The {@link RoutingContext} containing all relevant routing info.
   */
  private void handleGetCommentsForPost(RoutingContext ctx) {
    // Call a helper method to get the "post_id" route param from the routing context.
    int postId = getRequestParameterAsInt(ctx.request(), "post_id");

    try {
      // Get the list of comments.
      CommentsResponse response = processor.getCommentsForPost(postId);
      // Return the found comments.
      end(ctx.response(), 200, JsonObject.mapFrom(response).encode());
    } catch (IllegalArgumentException e) {
      // If an exception was thrown because there was no existing post with the given id, then end
      // with a 404 NOT FOUND.
      end(ctx.response(), 404, e.getMessage());
    }
  }

  /**
   * Register the POST "/posts" route.
   *
   * @param router The Router to register the route with.
   */
  private void registerPostPostsRoute(Router router) {
    Route route = router.post("/");
    route.handler(this::handlePostPostsRoute);
  }

  /**
   * Handle the POST "/posts" route
   *
   * @param ctx The {@link RoutingContext} containing all relevant routing info.
   */
  private void handlePostPostsRoute(RoutingContext ctx) {
    // Unmarshal the request body as a CreatePostRequest class.
    CreatePostRequest createPostRequest = getJsonBodyAsClass(ctx, CreatePostRequest.class);
    // Validate the DTO and ensure the provided info is valid.
    if (!createPostRequest.validate()) {
      end(ctx.response(), 400, "Create Post fields cannot be null.", "text/plain");
      return;
    }

    // Create the post using the processor.
    processor.createPost(createPostRequest);
    // Return a success.
    end(ctx.response(), 200);
  }
}
