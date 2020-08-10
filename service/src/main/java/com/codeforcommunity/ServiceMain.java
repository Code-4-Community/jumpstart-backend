package com.codeforcommunity;

import com.codeforcommunity.api.IPostsProcessor;
import com.codeforcommunity.processors.PostsProcessor;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.PostsRouter;

/**
 * The main class for this application. Sets up and starts the API server, and is our entry point
 * into the program.
 */
public class ServiceMain {

  /**
   * The main method. What gets called during the startup of this app.
   *
   * @param args A list of strings that gets passed through the command line when you call the app.
   *     For example, if the app were started like {@code java -jar myapp.jar --port=5000}, it would
   *     give you something like {@code ["--port=5000"]}.
   */
  public static void main(String[] args) {
    // Try to run our program, and catch any exception that isn't caught before this.
    try {
      // Instantiate a ServiceMain object.
      ServiceMain serviceMain = new ServiceMain();
      // Call the initialize method of that object.
      serviceMain.initialize();
    } catch (Exception e) {
      // Print the stack trace of the exception, and then exit the program.
      e.printStackTrace();
    }
  }

  /** Starts the server and initializes things that need to be initialized. */
  public void initialize() {
    initializeServer();
  }

  /** Sets up values that are needed and starts the API server. */
  private void initializeServer() {
    IPostsProcessor postsProcessor = new PostsProcessor();
    IRouter postsRouter = new PostsRouter(postsProcessor);
    ApiMain apiMain = new ApiMain(postsRouter);
    apiMain.startApi();
  }
}
