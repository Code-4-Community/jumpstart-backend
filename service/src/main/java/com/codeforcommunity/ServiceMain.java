package com.codeforcommunity;

/** The main class for this application. Sets up and starts the API server. */
public class ServiceMain {
  public static void main(String[] args) {
    try {
      ServiceMain serviceMain = new ServiceMain();
      serviceMain.initialize();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Starts the server and initializes things that need to be initialized. */
  public void initialize() {
    initializeServer();
  }

  /** Sets up values that are needed and starts the API server. */
  private void initializeServer() {
    ApiMain apiMain = new ApiMain();
    apiMain.startApi();
  }
}
