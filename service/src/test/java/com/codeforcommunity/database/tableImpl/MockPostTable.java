package com.codeforcommunity.database.tableImpl;

import com.codeforcommunity.database.records.PostRecord;
import java.util.Map;

/**
 * A Mock database class which will provide access to the underlying db so we can use it in testing.
 */
public class MockPostTable extends StubPostTableImpl {

  public MockPostTable() {
    super();
  }

  /**
   * Have a way to get the underlying map for testing purposes. It has public access, but is only
   * available in the test directory so that we can use it in tests without providing access to
   * everything else.
   *
   * @return The map that composes this db.
   */
  public Map<Integer, PostRecord> getUnderlyingDb() {
    return this.postMap;
  }
}
