package com.codeforcommunity.processor;

import com.codeforcommunity.api.IPostsProcessor;
import com.codeforcommunity.database.records.CommentRecord;
import com.codeforcommunity.database.records.PostRecord;
import com.codeforcommunity.database.table.ICommentTable;
import com.codeforcommunity.database.table.IPostTable;
import com.codeforcommunity.dto.request.CreateCommentRequest;
import com.codeforcommunity.dto.request.CreatePostRequest;
import com.codeforcommunity.dto.response.Comment;
import com.codeforcommunity.dto.response.CommentsResponse;
import com.codeforcommunity.dto.response.PostSummary;
import com.codeforcommunity.dto.response.PostsResponse;
import com.codeforcommunity.dto.response.SinglePostResponse;
import com.codeforcommunity.mapper.CommentMapper;
import com.codeforcommunity.mapper.PostMapper;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class which will handle all processing of posts. This includes creation, deletion, and
 * whatever else is needed.
 */
public class PostsProcessor implements IPostsProcessor {
  /**
   * Our in-memory 'database table' for holding posts. This will eventually be replaced by the real
   * db.
   */
  private final IPostTable postTable;
  /** Our in-memory 'database table' for holding comments. */
  private final ICommentTable commentTable;
  /** A max length we'll have set for our preview. */
  private static final int PREVIEW_MAX_LENGTH = 50;

  public PostsProcessor(IPostTable postTable, ICommentTable commentTable) {
    // Set the in memory database tables.
    this.postTable = postTable;
    this.commentTable = commentTable;
  }

  /**
   * Determine if the post exists and throw an exception if it doesn't.
   *
   * @param postId The ID of the post to check.
   */
  private void checkPostExists(int postId) {
    if (!postTable.postExists(postId)) {
      throw new IllegalArgumentException("No post with id " + postId + " does not exist.");
    }
  }

  /**
   * Determine if the given comment exists for the given posts and throw an exception if it doesn't.
   *
   * @param postId The ID the comment should belong to.
   * @param commentId The ID of the comment to check.
   */
  private void checkCommentExists(int postId, int commentId) {
    if (!commentTable.commentExists(postId, commentId)) {
      throw new IllegalArgumentException(
          "No comment with post id "
              + postId
              + " and comment id "
              + commentId
              + " does not exist.");
    }
  }

  @Override
  public PostsResponse getPosts() {
    // Get the PostRecords.
    List<PostRecord> posts = postTable.getAllPosts();

    // Loop through and set the comment counts for each post.
    for (PostRecord record : posts) {
      record.setCommentCount(commentTable.getCommentCountForPost(record.getId()));
    }

    // Turn the list into a stream, and map each PostRecord into a PostSummary using the PostMapper
    // interface that was created. After that, collect each object in the stream into a list.
    // A stream allows you to perform operations on a list. With it, you can do things like
    // filter/reduce, andmap, ormap, and a few other really useful operations. You can still use a
    // for loop like above though if you prefer!
    List<PostSummary> postSummaries =
        posts.stream().map(PostMapper::recordToSummary).collect(Collectors.toList());

    // Create a new PostsResponse and return that.
    return new PostsResponse(postSummaries);
  }

  @Override
  public SinglePostResponse getSinglePost(int postId) {
    this.checkPostExists(postId);

    // Return the post with the given postId.
    PostRecord post = postTable.getById(postId);
    return PostMapper.recordToResponse(post);
  }

  @Override
  public CommentsResponse getCommentsForPost(int postId) {
    this.checkPostExists(postId);

    // Get the comments belonging to the given postId. If none exist, an empty list is returned.
    List<CommentRecord> commentRecords = commentTable.getByPostId(postId);
    // Use the stream like described above to convert all CommentRecords to Comments.
    List<Comment> comments =
        commentRecords.stream().map(CommentMapper::recordToComment).collect(Collectors.toList());
    return new CommentsResponse(comments);
  }

  @Override
  public void createPost(CreatePostRequest post) {
    postTable.savePost(PostMapper.createRequestToRecord(post));
  }

  @Override
  public void createComment(int postId, CreateCommentRequest comment) {
    this.checkPostExists(postId);

    commentTable.saveComment(CommentMapper.createRequestToRecord(postId, comment));
  }

  @Override
  public void clapPost(int postId) {
    this.checkPostExists(postId);

    postTable.clapPost(postId);
  }

  @Override
  public void clapComment(int postId, int commentId) {
    this.checkPostExists(postId);
    this.checkCommentExists(postId, commentId);

    commentTable.clapComment(postId, commentId);
  }

  @Override
  public void deletePost(int postId) {
    this.checkPostExists(postId);

    // First delete all of the comments related to the post.
    commentTable.deleteCommentsByPostId(postId);

    // Then delete the post itself.
    postTable.deletePost(postId);
  }

  @Override
  public void deleteComment(int postId, int commentId) {
    this.checkPostExists(postId);
    this.checkCommentExists(postId, commentId);

    commentTable.deleteComment(postId, commentId);
  }

  /**
   * Get a substring which is at most {@code previewMaxLength} length but possibly smaller.
   *
   * @param body The body to extract the preview from.
   * @return A string representing the preview.
   */
  public static String getBodyPreview(String body) {
    // Select a substring which is previewMaxLength or the length of the body if that is shorter.
    // We don't want to be getting any issues by selecting 50 characters if the body length
    // is only 20 characters.
    return body.substring(0, Math.min(PREVIEW_MAX_LENGTH, body.length()));
  }
}
