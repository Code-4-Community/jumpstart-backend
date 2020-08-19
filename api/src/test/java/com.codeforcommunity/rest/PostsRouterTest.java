package com.codeforcommunity.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeforcommunity.api.IPostsProcessor;
import com.codeforcommunity.dto.response.PostSummary;
import com.codeforcommunity.dto.response.PostsResponse;
import com.codeforcommunity.dto.response.SinglePostResponse;
import com.codeforcommunity.rest.PostsRouter.Externals;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * This class is to test the router. Usually, we don't end up testing the router, but we think it
 * would be pretty useful to show you the Mockito dependency. Because this is purely for that, we'll
 * only add a couple examples.
 *
 * <p>Mockito is pretty cool, since it allows you to define what happens and what's returned/thrown
 * when a method is called. It also lets you verify methods are called (with specific params if you
 * want!), capture those params for testing, spy on existing objects so you can define the bare
 * minimum if you want, and some other interesting functionality. Go check it out!
 *
 * <p>Check out the Mockito documentation for more info
 * https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html.
 */
public class PostsRouterTest {
  private PostsRouter router;
  private IPostsProcessor processor;
  private Vertx vertx;
  private Router vertxRouter;
  private Route route;
  private RoutingContext ctx;
  private HttpServerResponse res;

  @BeforeEach
  public void setup() {
    // The mock method allows us to create a fake version of the Vertx class (which we usually don't
    // have a lot
    // of control over) where we can simply write the interactions we want it to have.
    this.vertx = mock(Vertx.class);
    // Remember, we don't have access to this since the api module doesn't have a dependency on the
    // service module (that would create a dependency cycle, which is really bad).
    this.processor = mock(IPostsProcessor.class);
    this.router = new PostsRouter(this.processor, new TestExternals());
    this.vertxRouter = mock(Router.class);
    this.route = mock(Route.class);
    this.ctx = mock(RoutingContext.class);

    // Mock for the IRouter#end method. We don't have to handle HttpServerResponse#end method, since
    // unmocked methods just return null.
    res = mock(HttpServerResponse.class);

    // When the setStatusCode() method is called on res with any int as the parameter, then return
    // res
    when(res.setStatusCode(anyInt())).thenReturn(res);
    // When the putHeader() method is called on res with 2 strings as the parameter, then return res
    when(res.putHeader(anyString(), anyString())).thenReturn(res);
    // When the response() method is called on the ctx, then return res
    when(ctx.response()).thenReturn(res);

    /*
     * What's going on here is that we're calling the *when* method, which allows us to define what
     * happens when a certain method is called on a mocked object.
     *
     * The anyString() method allows us to define exactly which method (for
     * example, if the method is overloaded) and which parameters to perform the *then* action on.
     * *anyString* will react to any string (and *anyInt* will react to any int), but you can also
     * add in exactly what you're expecting, like "/" or "hello world!", to define exactly when you
     * want this action to happen (so you can define multiple actions that
     * happen on different parameters).
     *
     * Finally, the thenReturn lets you specify what you want to be returned
     * (or what to throw/do in the case of
     * other *then*s.
     *
     * <p>Here we're just setting up for responses in the future, since register routes will call
     * this with all routes. Wow that's a lot of text.
     */

    // When get() method is called on the vertxRouter with a string, then return the route variable
    // we defined above
    when(vertxRouter.get(anyString())).thenReturn(route);
    // When post() method is called on the vertxRouter with a string, then return the route variable
    // we defined above
    when(vertxRouter.post(anyString())).thenReturn(route);
    // When delete() method is called on the vertxRouter with a string, then return the route
    // variable we defined above
    when(vertxRouter.delete(anyString())).thenReturn(route);
  }

  /**
   * Our overridden {@link Externals}. We could do this by mocking it, but both ways are pretty
   * similar, so we'll do it this way.
   *
   * <p>Notice how this isn't a *static* class. That's because we want access to the vertxRouter
   * that's in the wrapping class, so we'll need an instance of that available to the TestExternals.
   */
  private class TestExternals extends Externals {

    @Override
    public Router getRouter(Vertx vertx) {
      return vertxRouter;
    }
  }

  private SinglePostResponse generatePost(int id) {
    // We don't really care about having a bunch of random data here. This is more about returning a
    // valid object, since that will be tested more in the service tests.
    return new SinglePostResponse(
        id, "author " + id, "today's date", "title", 500, "this is a body");
  }

  private PostsResponse generatePosts(int count) {
    List<PostSummary> posts = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      SinglePostResponse post = generatePost(i);
      String preview = post.getBody().substring(0, Math.min(50, post.getBody().length()));
      posts.add(new PostSummary(post, preview, i));
    }
    return new PostsResponse(posts);
  }

  @Test
  public void testGetPosts() {
    // Set up for the get route specifically.
    Route getRoute = mock(Route.class);
    when(vertxRouter.get("/")).thenReturn(getRoute);

    router.initializeRouter(vertx);

    // Just to show you one, this is how we would test something (using an any...) that might be
    // called
    // more than once. There's also an atMostOnce(), times(<some number>), and a few other counting
    // params.
    verify(vertxRouter, atLeastOnce()).get(anyString());
    // If it's just once you're expecting, you can leave it off though.
    verify(vertxRouter).get("/");

    // Here's how to capture an argument used. We can then test things about it.
    ArgumentCaptor<Handler<RoutingContext>> handlerArgumentCaptor =
        ArgumentCaptor.forClass(Handler.class);
    verify(getRoute).handler(handlerArgumentCaptor.capture());

    // Here is the 'private' PostsRouter::handleGetPostsRoute handler. We can now test stuff about
    // it.
    Handler<RoutingContext> handler = handlerArgumentCaptor.getValue();

    // But first we need to prepare for the call to PostsRouter::getPosts.
    when(processor.getPosts()).thenReturn(generatePosts(5));

    // And run the handler.
    handler.handle(ctx);

    // Finally, get the encoded String using an ArgumentCaptor, and make sure it's what we're
    // expecting.
    // We could also test the status code and headers, but we're not too worried about those.
    ArgumentCaptor<String> encodedResponse = ArgumentCaptor.forClass(String.class);
    verify(res).end(encodedResponse.capture());
    assertEquals(
        "{\"posts\":[{\"id\":0,\"author\":\"author 0\",\"dateCreated\":\"today's date\","
            + "\"title\":\"title\",\"clapCount\":500,\"preview\":\"this is a body\",\"commentCount\":0},"
            + "{\"id\":1,\"author\":\"author 1\",\"dateCreated\":\"today's date\",\"title\":\"title\","
            + "\"clapCount\":500,\"preview\":\"this is a body\",\"commentCount\":1},"
            + "{\"id\":2,\"author\":\"author 2\",\"dateCreated\":\"today's date\",\"title\":\"title\","
            + "\"clapCount\":500,\"preview\":\"this is a body\",\"commentCount\":2},{\"id\":3,\"author\":"
            + "\"author 3\",\"dateCreated\":\"today's date\",\"title\":\"title\",\"clapCount\":500,"
            + "\"preview\":\"this is a body\",\"commentCount\":3},{\"id\":4,\"author\":\"author 4\","
            + "\"dateCreated\":\"today's date\",\"title\":\"title\",\"clapCount\":500,\"preview\":"
            + "\"this is a body\",\"commentCount\":4}]}",
        encodedResponse.getValue());
  }
}
