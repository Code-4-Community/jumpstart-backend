package com.codeforcommunity.processor;

import static com.codeforcommunity.database.seeder.Seeder.STUB_POST_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.codeforcommunity.database.records.CommentRecord;
import com.codeforcommunity.database.records.PostRecord;
import com.codeforcommunity.database.seeder.Seeder;
import com.codeforcommunity.database.tableImpl.MockCommentTable;
import com.codeforcommunity.database.tableImpl.MockPostTable;
import com.codeforcommunity.dto.request.CreateCommentRequest;
import com.codeforcommunity.dto.request.CreatePostRequest;
import com.codeforcommunity.dto.response.Comment;
import com.codeforcommunity.dto.response.CommentsResponse;
import com.codeforcommunity.dto.response.PostSummary;
import com.codeforcommunity.dto.response.PostsResponse;
import com.codeforcommunity.dto.response.SinglePostResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * A class to test our processor and make sure everything works as expected. The tests in this class
 * will be run by Maven when building your project.
 */
public class PostsProcessorTest {
  // We're using the mock table instance rather than the interface since we want access to the
  // methods created for testing.
  private MockCommentTable commentTable;
  private MockPostTable postTable;
  private PostsProcessor processor;

  /**
   * The {@code @BeforeEach} annotation lets JUnit know to run this method before running each test.
   * This lets us clear anything that may have been in the database or changed in the previous
   * processor so that we know each test will be run from the same starting point.
   */
  @BeforeEach
  public void setup() {
    // Setup a new CommentTable seeded with STUB_POST_COUNT posts.
    this.commentTable = new MockCommentTable();
    // Setup a new PostTable.
    this.postTable = new MockPostTable();
    // Seed the databases.
    Seeder.seedDatabase(STUB_POST_COUNT, postTable, commentTable);
    // Setup a new PostsProcessor.
    this.processor = new PostsProcessor(this.postTable, this.commentTable);
  }

  @Test
  public void testGetPosts() {
    // Get the posts from the processor.
    PostsResponse posts = processor.getPosts();
    // Make sure we have STUB_POST_COUNT posts.
    assertEquals(STUB_POST_COUNT, posts.getPosts().size());

    // Check each post to make sure it has properties we expect.
    // Mainly that no fields are null, the date isn't blank, and integers are 0 or positive.
    for (PostSummary post : posts.getPosts()) {
      assertNotNull(post.getId());
      assertTrue(post.getId() >= 0);
      assertNotNull(post.getAuthor());
      assertNotNull(post.getTitle());
      assertNotNull(post.getClapCount());
      assertTrue(post.getClapCount() >= 0);
      assertNotNull(post.getDateCreated());
      assertFalse(post.getDateCreated().isBlank());
      assertNotNull(post.getPreview());

      // Make sure the preview's length is less than or equal to the body's length.
      PostRecord record = postTable.getUnderlyingDb().get(post.getId());
      assertTrue(record.getBody().length() >= post.getPreview().length());
    }
  }

  @Test
  public void testGetComments() {
    // For each post that (should) exists.
    for (int i = 0; i < STUB_POST_COUNT; i++) {
      // Get the posts from the processor.
      CommentsResponse comments = processor.getCommentsForPost(i);

      // Check each post to make sure it has properties we expect.
      // Mainly that no fields are null, the date isn't blank, and integers are 0 or positive.
      // The postId should also be equal to the current post.
      for (Comment comment : comments.getComments()) {
        assertNotNull(comment.getId());
        assertTrue(comment.getId() >= 0);
        assertEquals(i, comment.getPostId());
        assertNotNull(comment.getAuthor());
        assertNotNull(comment.getBody());
        assertNotNull(comment.getClapCount());
        assertTrue(comment.getClapCount() >= 0);
        assertNotNull(comment.getDateCreated());
        assertFalse(comment.getDateCreated().isBlank());
      }
    }
  }

  // Write a test which runs multiple times with different inputs.
  @ParameterizedTest
  @ValueSource(ints = {1, 2, 5, 14})
  public void testGetSinglePost(int postId) {
    // Make sure that getting a single post returns a post with that ID.
    SinglePostResponse post = processor.getSinglePost(postId);
    assertEquals(postId, post.getId());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, STUB_POST_COUNT + 1})
  public void testGetSinglePostInvalidPostId(int postId) {
    // Try to get a post whose ID does not exist. We're checking exceptions like this because
    // the try/catch method allows us to check things about the thrown exception.
    try {
      processor.getSinglePost(postId);
      // Fail this test if an exception isn't thrown.
      fail("A non-existent post was able to be retrieved.");
    }
    catch (IllegalArgumentException e) {
      // Check that the message is what we expect.
      String message = "Post with id " + postId + " does not exist.";
      assertEquals(message, e.getMessage());
      // Make sure the number of posts stayed the same.
      assertEquals(STUB_POST_COUNT, postTable.getUnderlyingDb().size());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, STUB_POST_COUNT + 1})
  public void testGetCommentsInvalidPostId(int postId) {
    // Try to get comments for a post that doesn't exist.
    try {
      processor.getCommentsForPost(postId);
      // Fail if an exception isn't thrown.
      fail("Comments were able to be retrieved for a post that does not exist.");
    }
    catch (IllegalArgumentException e) {
      // Check the exception message.
      String message = "Post with id " + postId + " does not exist.";
      assertEquals(message, e.getMessage());
    }
  }

  @Test
  public void testCreatePost() {
    // Create a new post.
    String author = "PostAuthor";
    String title = "PostTitle";
    String body = "PostBody";
    CreatePostRequest newPost = new CreatePostRequest(author, title, body);

    processor.createPost(newPost);

    // Check that the expected values are in the most recently added post.
    Map<Integer, PostRecord> postMap = postTable.getUnderlyingDb();
    // Remember that the IDs are 0-indexed.
    PostRecord recentPost = postMap.get(postMap.size() - 1);
    assertEquals(author, recentPost.getAuthor());
    assertEquals(title, recentPost.getTitle());
    assertEquals(body, recentPost.getBody());
    // Make sure the count of posts has increased by 1.
    assertEquals(STUB_POST_COUNT + 1, postMap.size());
  }

  @Test
  public void testCreateComment() {
    // Create new comment.
    String author = "CommentAuthor";
    String body = "CommentAuthor";
    CreateCommentRequest newComment = new CreateCommentRequest(author, body);

    processor.createComment(0, newComment);

    // Check that the expected values are in the most recently added comment.
    Map<Integer, Map<Integer, CommentRecord>> commentMap = commentTable.getUnderlyingDb();
    CommentRecord comment = commentMap.get(0).get(commentMap.get(0).size() - 1);
    assertEquals(author, comment.getAuthor());
    assertEquals(body, comment.getBody());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, STUB_POST_COUNT + 1})
  public void testCreateCommentInvalidPost(int postId) {
    CreateCommentRequest newComment = new CreateCommentRequest("", "");

    try {
      // Try to add a comment to an invalid post.
      processor.createComment(postId, newComment);
      fail("A comment was able to be created for an invalid post.");
    }
    catch (IllegalArgumentException e) {
      // Check the exception message.
      String message = "Post with id " + postId + " does not exist.";
      assertEquals(message, e.getMessage());
    }
  }

  @Test
  public void testClapPost() {
    // Get the post with ID 1.
    PostRecord post = postTable.getById(1);
    // Get the clap count. This value won't change since it's an atomic data type.
    int clapCount = post.getClapCount();

    // Clap the post.
    processor.clapPost(1);
    // Check that the value changed.
    assertEquals(clapCount + 1, post.getClapCount());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, STUB_POST_COUNT + 1})
  public void testClapInvalidPost(int postId) {
    try {
      // Try to add a comment to an invalid post.
      processor.clapPost(postId);
      fail("A non-existent comment was able to be clapped.");
    }
    catch (IllegalArgumentException e) {
      // Check the exception message.
      String message = "Post with id " + postId + " does not exist.";
      assertEquals(message, e.getMessage());
    }
  }

  @Test
  public void testClapComment() {
    // Create and get a comment ID since we can't for sure say it exists initially.
    // This is because of the way we created the data generation, a random amount of comments
    // are created on each run.
    CreateCommentRequest commentRequest = new CreateCommentRequest("", "");
    processor.createComment(0, commentRequest);
    // Remember that the IDs are 0-indexed.
    int commentId = commentTable.getUnderlyingDb().get(0).size() - 1;
    CommentRecord comment = commentTable.getByPostId(0).get(commentId);
    // Get the clap count. This value won't change since it's an atomic data type.
    int clapCount = comment.getClapCount();

    // Clap the comment.
    processor.clapComment(0, commentId);
    // Check that the value changed.
    assertEquals(clapCount + 1, comment.getClapCount());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, STUB_POST_COUNT + 1})
  public void testClapCommentInvalidPost(int postId) {
    try {
      // Try to add a comment to an invalid post.
      processor.clapComment(postId, 0);
      fail("A comment was able to be clapped on a non-existent post.");
    }
    catch (IllegalArgumentException e) {
      // Check the exception message.
      String message = "Post with id " + postId + " does not exist.";
      assertEquals(message, e.getMessage());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, STUB_POST_COUNT + 1})
  public void testClapCommentInvalidComment(int commentId) {
    try {
      // Try to add a comment to an invalid post.
      processor.clapComment(0, commentId);
      fail("A non-existent comment was able to be clapped.");
    }
    catch (IllegalArgumentException e) {
      // Check the exception message.
      String message = "Comment with id " + commentId + " does not exist.";
      assertEquals(message, e.getMessage());
    }
  }

  @Test
  public void testDeletePost() {
    // Make sure the post exists initially.
    assertNotNull(processor.getSinglePost(1));
    assertTrue(postTable.getUnderlyingDb().containsKey(1));

    // Delete the post.
    processor.deletePost(1);
    try {
      // Try to get the post again.
      processor.getSinglePost(1);
      fail("A post was successfully retrieved when it should have been deleted.");
    }
    catch (IllegalArgumentException e) {
      // Check the exception message.
      String message = "Post with id 1 does not exist.";
      assertEquals(message, e.getMessage());
      // Make sure the count of posts has been decreased by 1.
      assertEquals(STUB_POST_COUNT - 1, postTable.getUnderlyingDb().size());
      assertFalse(postTable.getUnderlyingDb().containsKey(1));
      assertFalse(commentTable.getUnderlyingDb().containsKey(1));
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, STUB_POST_COUNT + 1})
  public void testDeleteInvalidPost(int postId) {
    try {
      // Try to delete an invalid post.
      processor.deletePost(postId);
      fail("A non-existent post was able to be deleted.");
    }
    catch (IllegalArgumentException e) {
      // Check the exception message.
      String message = "Post with id " + postId + " does not exist.";
      assertEquals(message, e.getMessage());
    }
  }

  @Test
  public void testDeleteComment() {
    // Make sure the post exists initially.
    assertNotNull(processor.getSinglePost(0));
    assertTrue(postTable.getUnderlyingDb().containsKey(0));

    // Create and get a comment ID since we can't for sure say it exists initially.
    // This is because of the way we created the data generation, a random amount of comments
    // are created on each run.
    CreateCommentRequest commentRequest = new CreateCommentRequest("", "");
    processor.createComment(0, commentRequest);
    int commentId = commentTable.getUnderlyingDb().get(0).size() - 1;
    int countOfCommentsForPost = commentTable.getUnderlyingDb().get(0).size();

    // Delete the comment.
    processor.deleteComment(0, commentId);

    // Make sure the comment doesn't exist.
    assertFalse(commentTable.getUnderlyingDb().get(0).containsKey(commentId));
    assertEquals(countOfCommentsForPost - 1, commentTable.getUnderlyingDb().get(0).size());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, STUB_POST_COUNT + 1})
  public void testDeleteCommentInvalidPost(int postId) {
    try {
      // Try to delete an invalid post.
      processor.deleteComment(postId, 0);
      fail("A non-existent comment was able to be deleted.");
    }
    catch (IllegalArgumentException e) {
      // Check the exception message.
      String message = "Post with id " + postId + " does not exist.";
      assertEquals(message, e.getMessage());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, STUB_POST_COUNT + 1})
  public void testDeleteCommentInvalidComment(int commentId) {
    try {
      // Try to delete an invalid comment.
      processor.deleteComment(0, commentId);
      fail("A non-existent post was able to be deleted.");
    }
    catch (IllegalArgumentException e) {
      // Check the exception message.
      String message = "Comment with id " + commentId + " does not exist.";
      assertEquals(message, e.getMessage());
    }
  }
}
