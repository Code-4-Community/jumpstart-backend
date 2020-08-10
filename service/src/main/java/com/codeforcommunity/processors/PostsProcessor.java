package com.codeforcommunity.processors;

import com.codeforcommunity.api.IPostsProcessor;
import com.codeforcommunity.dto.response.PostSummary;
import com.codeforcommunity.dto.response.PostsResponse;
import com.codeforcommunity.dto.response.SinglePostResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The class which will handle all processing of posts. This includes creation, deletion, and
 * whatever else is needed.
 */
public class PostsProcessor implements IPostsProcessor {
  // Our in-memory 'database' for holding posts. This will eventually be replaced by the real db.
  private final Map<Integer, SinglePostResponse> postMap;
  // A count of how many posts we want to make on startup for the in memory db. You can change this
  // to be whatever you want.
  private static final int stubPostCount = 15;
  // A max length we'll have set for our preview.
  private static final int previewMaxLength = 50;
  // The classic Lorem Ipsum text. For now this will be our stubbed data.
  private static final String loremIpsumText =
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

  public PostsProcessor() {
    // Set the postMap.
    postMap = new HashMap<>();
    // Initialize a new Random object.
    Random random = new Random();

    // Loop through and create stubPostCount amount of posts.
    for (int i = 0; i < stubPostCount; i++) {
      // Build a date string. For each number to be inserted, a random number from [0, bound) is
      // selected, and then is transformed to look more like a date. 1 is added to the day and month
      // so that you'll have days between [1, 30], and months between [1, 12]. 2000 is added to the
      // year so you'll get years between 2000 and 2020.
      String date =
          String.format(
              "%d/%d/%d",
              random.nextInt(12) + 1, random.nextInt(30) + 1, random.nextInt(21) + 2000);
      // Get a length we'll want to use to select text.
      int textLength = random.nextInt(loremIpsumText.length());
      // Select a substring of textLength characters from the Lorem Ipsum text.
      String body = loremIpsumText.substring(0, textLength);
      // Create a new post with the generated data from above.
      SinglePostResponse post =
          new SinglePostResponse(
              i, "Author " + i % 5, date, "Post #" + i, random.nextInt(1000), body);
      // Put our post in the map.
      postMap.put(i, post);
    }
  }

  @Override
  public PostsResponse getPosts() {
    // Get the values from the postMap, convert them to a Post[] (by passing in a new Post[] with no
    // space allocated), and then turn it into a List.
    List<SinglePostResponse> posts =
        Arrays.asList(postMap.values().toArray(new SinglePostResponse[0]));

    List<PostSummary> summaries = new ArrayList<>();

    for (SinglePostResponse post : posts) {
      // Select a substring which is previewMaxLength or the length of the body if that is shorter.
      // We don't want to be getting any issues by selecting 50 characters if the body length
      // is only 20 characters.
      String preview =
          post.getBody().substring(0, Math.min(previewMaxLength, post.getBody().length()));

      PostSummary summary = new PostSummary(post, preview);
      summaries.add(summary);
    }

    // Create a new PostsResponse and return that.
    return new PostsResponse(summaries);
  }

  @Override
  public SinglePostResponse getSinglePost(int postId) {
    // Return the post with the given postId. If none exists, null is returned which is handled in
    // the route.
    return postMap.get(postId);
  }
}
