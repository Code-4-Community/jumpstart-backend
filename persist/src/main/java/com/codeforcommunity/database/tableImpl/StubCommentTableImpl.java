package com.codeforcommunity.database.tableImpl;

import com.codeforcommunity.database.records.CommentRecord;
import com.codeforcommunity.database.table.ICommentTable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // Get the list of comments pertaining to the comment's post id. If empty, create a new list to
    // store comments in.
    commentMap.putIfAbsent(comment.getPostId(), new HashMap<>());
    // Add the given comment to our (possibly newly created) list.
    commentMap.get(comment.getPostId()).put(comment.getId(), comment);
  }
}
