package com.codeforcommunity.database.tableImpl;

import com.codeforcommunity.database.records.CommentRecord;
import com.codeforcommunity.database.seeder.Seeder;
import com.codeforcommunity.database.table.ICommentTable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Our implementation of the {@link ICommentTable} in our database. This class will eventually be
 * replaced once we start actually working with the database, but we'll still use it for testing
 * purposes.
 */
public class StubCommentTableImpl implements ICommentTable {
  /**
   * A map of an integers (representing a postId) to a Map of that post's comment IDs to comments.
   * If it helps to visualize in JSON, that would look like this:
   *
   * <pre>
   * {
   *   1: {
   *     1: {
   *       "id": 1,
   *       "postId": 1,
   *       "comment author": "me",
   *       ...
   *     },
   *     3: {
   *       "id": 3,
   *       "postId": 1,
   *       ...
   *     }
   *     ...
   *   }
   *   5: {
   *     2: {
   *       "id": 2,
   *       "postId": 5,
   *       "author": "???",
   *       ...
   *     },
   *     ...
   *   }
   * }
   * </pre>
   *
   * @see StubPostTableImpl for a better {@link HashMap}/{@link Map} explanation.
   */
  private final Map<Integer, Map<Integer, CommentRecord>> commentMap;

  public StubCommentTableImpl() {
    this.commentMap = new HashMap<>();
  }

  @Override
  public List<CommentRecord> getByPostId(int postId) {
    // Get the Map of comment IDs to comments, or an empty hashmap if none exist for the given id.
    Map<Integer, CommentRecord> commentMap = this.commentMap.getOrDefault(postId, new HashMap<>());
    // Return a copy of the values in the post's map of comments
    return List.copyOf(commentMap.values());
  }

  @Override
  public void saveComment(CommentRecord comment) {
    // Once we start using the database, these operations will be handled for us.
    comment.setId(this.getLastId(comment.getPostId()) + 1);
    comment.setDateCreated(Seeder.getCurrentDateTime());
    comment.setClapCount(0);

    // Get the list of comments pertaining to the comment's post id. If empty, create a new list to
    // store comments in.
    commentMap.putIfAbsent(comment.getPostId(), new HashMap<>());
    // Add the given comment to our (possibly newly created) list.
    commentMap.get(comment.getPostId()).put(comment.getId(), comment);
  }

  @Override
  public boolean commentExists(int postId, int commentId) {
    // Determine if the given post has comments, and if so, determine if there are any with the
    // given id.
    return commentMap.containsKey(postId) && commentMap.get(postId).containsKey(commentId);
  }

  @Override
  public void clapComment(int postId, int commentId) {
    // Get the comments relating to the post.
    Map<Integer, CommentRecord> comments = commentMap.get(postId);
    // Find the comment with the given commentId.
    CommentRecord record = comments.get(commentId);
    record.setClapCount(record.getClapCount() + 1);
  }

  @Override
  public void deleteCommentsByPostId(int postId) {
    commentMap.remove(postId);
  }

  @Override
  public void deleteComment(int postId, int commentId) {
    // Remove the comment from the list of comments related to the postId.
    commentMap.get(postId).remove(commentId);
  }

  /**
   * Get the ID of the most recently inserted item. This is so that we can artificially assign a
   * valid ID to the next item being inserted.
   *
   * @return An integer representing the most recent ID.
   */
  private int getLastId(int postId) {
    Optional<Integer> maxId =
        this.commentMap.getOrDefault(postId, new HashMap<>()).keySet().stream()
            .max(Integer::compareTo);
    return maxId.orElse(-1);
  }
}
