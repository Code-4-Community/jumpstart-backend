package com.codeforcommunity.database.tableImpl;

import com.codeforcommunity.database.records.CommentRecord;
import java.util.Map;

/**
 * A Mock database class which will provide access to the underlying db so we can use it in testing.
 */
public class MockCommentTable extends StubCommentTableImpl {

  public MockCommentTable() {
    super();
  }

  /**
   * Have a way to get the underlying map for testing purposes. It has public access, but is only
   * available in the test directory so that we can use it in tests without providing access to
   * everything else.
   *
   * @return The map that composes this db.
   */
  public Map<Integer, Map<Integer, CommentRecord>> getUnderlyingDb() {
    return this.commentMap;
  }
}
