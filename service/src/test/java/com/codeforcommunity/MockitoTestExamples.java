package com.codeforcommunity;

import static com.codeforcommunity.database.seeder.Seeder.STUB_POST_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeforcommunity.database.records.PostRecord;
import com.codeforcommunity.database.seeder.Seeder;
import com.codeforcommunity.database.table.IPostTable;
import com.codeforcommunity.database.tableImpl.MockCommentTable;
import com.codeforcommunity.database.tableImpl.MockPostTable;
import com.codeforcommunity.dto.request.CreatePostRequest;
import com.codeforcommunity.processor.PostsProcessor;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MockitoTestExamples {
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

  // Let's redo the CreatePostRequest test from PostsProcessorTest using Mockito.
  @Test
  public void mockitoCreatePostRequestTest() {
    // Create a new post.
    String author = "PostAuthor";
    String title = "PostTitle";
    String body = "PostBody";

    // Create a mock of the CreatePostRequest class. We usually do this when we have a dependency
    // we want to set up without a ton of work. When we have this, we can mock JUST the things
    // we want to see used. This can involve throwing exceptions when things are called, returning
    // results (like we're doing below), and returning the result of another method we specify.
    CreatePostRequest newPost = Mockito.mock(CreatePostRequest.class);
    Mockito.when(newPost.getAuthor()).thenReturn("PostAuthor");
    Mockito.when(newPost.getTitle()).thenReturn("PostTitle");
    Mockito.when(newPost.getBody()).thenReturn("PostBody");

    // Don't forget that CreatePostRequest has a validate method, but we didn't set it up at all!
    // Run createPost with our mocked CreatePostRequest.
    processor.createPost(newPost);

    // Notice how everything here runs the same as before!
    // Check that the expected values are in the most recently added post.
    Map<Integer, PostRecord> postMap = postTable.getUnderlyingDb();
    // Remember that the IDs are 0-indexed.
    PostRecord recentPost = postMap.get(postMap.size() - 1);
    assertEquals(author, recentPost.getAuthor());
    assertEquals(title, recentPost.getTitle());
    assertEquals(body, recentPost.getBody());
    // Make sure the count of posts has increased by 1.
    assertEquals(STUB_POST_COUNT + 1, postMap.size());

    // Verify that the newPost.get... methods have been called once.
    Mockito.verify(newPost).getAuthor();
    Mockito.verify(newPost).getBody();
    Mockito.verify(newPost).getTitle();
    // Verify that newPost.validate() has never been called.
    Mockito.verify(newPost, Mockito.never()).validate();
  }

  // Let's show an example of mocking an interface. Pretend that IPostTable is a complex library
  // created by an open source project for us. When we're testing, we don't want to do a ton of
  // work to set it up, so we're going to mock it!
  @Test
  public void mockitoValidateWithAnyInt() {
    // Look, we can even mock interfaces!
    IPostTable mockPostTable = Mockito.mock(IPostTable.class);
    // Set up what to do when postExists is called.
    Mockito.when(mockPostTable.postExists(Mockito.anyInt())).thenReturn(true);

    // Set up what to do when deletePost is called.
    // Note: since deletePost is a void method, we have to set it up using doThrow instead.
    String msg = "Oh no! You weren't supposed to call that!";
    // It's only going to get thrown when called with ID 5.
    Mockito.doThrow(new IllegalArgumentException(msg)).when(mockPostTable).deletePost(5);

    // We can now set up our processor with our mocked database.
    PostsProcessor newProcessor = new PostsProcessor(mockPostTable, this.commentTable);

    // Run the method.
    newProcessor.deletePost(100);

    // Make sure that postExists was called! We can use either 100 or anyInt() for this, depending
    // on whether or not we want to make sure that it was called with 100 or
    // just see if it was called.
    Mockito.verify(mockPostTable).postExists(100);
    // Make sure deletePost was called too! Let's switch it up and call it with anyInt() this time.
    Mockito.verify(mockPostTable).deletePost(Mockito.anyInt());

    // Now we're going to make it throw the exception.
    try {
      newProcessor.deletePost(5);
    } catch (IllegalArgumentException e) {
      // See if the messages are the same.
      assertEquals(msg, e.getMessage());
    }

    // Let's verify again. This time, since it's been called twice, we need to let Mockito know
    // we're expecting it to have been called more than once.
    Mockito.verify(mockPostTable, Mockito.times(2)).postExists(Mockito.anyInt());
    // But it should have only been called once with id = 5.
    Mockito.verify(mockPostTable).postExists(5);
    // And we can make sure deletePost was called the same way.
    Mockito.verify(mockPostTable, Mockito.times(2)).deletePost(Mockito.anyInt());
    Mockito.verify(mockPostTable).deletePost(5);
  }
}
