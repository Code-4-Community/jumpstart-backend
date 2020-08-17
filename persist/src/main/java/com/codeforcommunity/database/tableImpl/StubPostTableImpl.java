package com.codeforcommunity.database.tableImpl;

import com.codeforcommunity.database.records.PostRecord;
import com.codeforcommunity.database.seeder.Seeder;
import com.codeforcommunity.database.table.IPostTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Our implementation of the {@link IPostTable} in our database. This class will eventually be
 * replaced once we start actually working with the database, but we'll still use it for testing
 * purposes.
 */
public class StubPostTableImpl implements IPostTable {
  /**
   * Our in-memory database. This is a {@link HashMap}/{@link Map}, which associates each object it
   * contains with a key. In this case, each post we will put in it will be identified by its
   * integer id. In other languages, this is pretty close to a dictionary, hash (the data structure
   * kind), or map.
   *
   * <p>What makes these interesting is that you can find a value from a given key in constant time.
   * The two basic methods you'll want to use on this are {@link Map#put(Object, Object)} and {@link
   * Map#get(Object)}. Until you learn about these in Fundies 2 though, please stick to keeping
   * types provided by Java (like Strings, Integers, ...) as your key, otherwise you might encounter
   * weird interactions.
   *
   * <p>If it helps to see an example of this map in JSON as a way to imagine it, here is an
   * example. This JSON uses types Integer to PostRecord, commonly written like {@code Map<Integer,
   * PostRecord>}.
   *
   * <pre>
   *   {
   *     1: {
   *       "id": 1,
   *       "author": "me",
   *       ...
   *     },
   *     3: {
   *       "id": 3,
   *       "author": "you",
   *       ...
   *     },
   *     ...
   *   }
   * </pre>
   */
  private final Map<Integer, PostRecord> postMap;

  public StubPostTableImpl() {
    this.postMap = new HashMap<>();
  }

  @Override
  public PostRecord getById(int id) {
    // Make sure this post exists before returning it.
    if (!this.postExists(id)) {
      throw new IllegalArgumentException("No post with ID " + id + "exists");
    }

    return this.postMap.get(id);
  }

  @Override
  public List<PostRecord> getAllPosts() {
    // Create a new array list containing the postMap's values. The reason we have to wrap that in
    // an array list is because Map.values() returns a Collection, which isn't a list.
    List<PostRecord> posts = new ArrayList<>(this.postMap.values());
    return posts;
  }

  @Override
  public void savePost(PostRecord post) {
    // Once we start using the database, these operations will be handled for us.
    post.setId(this.getLastId() + 1);
    post.setDateCreated(Seeder.getCurrentDateTime());
    post.setClapCount(0);

    this.postMap.put(post.getId(), post);
  }

  @Override
  public boolean postExists(int postId) {
    // See if a post with the given ID exists.
    return this.postMap.containsKey(postId);
  }

  @Override
  public void clapPost(int postId) {
    PostRecord record = postMap.get(postId);
    record.setClapCount(record.getClapCount() + 1);
  }

  @Override
  public void deletePost(int postId) {
    if (!this.postExists(postId)) {
      throw new IllegalArgumentException("No post with ID " + postId + "exists");
    }

    postMap.remove(postId);
  }

  /**
   * Get the ID of the most recently inserted item. This is so that we can artificially assign a
   * valid ID to the next item being inserted.
   *
   * @return An integer representing the most recent ID.
   */
  private int getLastId() {
    Optional<Integer> maxId = this.postMap.keySet().stream().max(Integer::compareTo);
    return maxId.orElse(-1);
  }
}
