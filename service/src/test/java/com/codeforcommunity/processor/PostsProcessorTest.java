package com.codeforcommunity.processor;

import static com.codeforcommunity.database.seeder.Seeder.STUB_POST_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeforcommunity.database.records.PostRecord;
import com.codeforcommunity.database.seeder.Seeder;
import com.codeforcommunity.database.tableImpl.MockCommentTable;
import com.codeforcommunity.database.tableImpl.MockPostTable;
import com.codeforcommunity.dto.response.Comment;
import com.codeforcommunity.dto.response.CommentsResponse;
import com.codeforcommunity.dto.response.PostSummary;
import com.codeforcommunity.dto.response.PostsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
