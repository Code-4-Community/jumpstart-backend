-- If you want to have data in your database, you can run this script. Also, feel free to add whatever
-- data you want in here!
-- P.S. If you run this script multiple times, you will have duplicate posts.

INSERT INTO posts (author, title, body) VALUES
('You', 'First Post!', 'Hey, this is my first post in the project I made for Jumpstart!'),
('Jumpstart Team', 'Congrats', 'Congrats on getting your database up and running!');

INSERT INTO comments (post_id, author, body) VALUES
((SELECT id - 1 FROM posts ORDER BY id DESC LIMIT 1), 'Jumpstart Team', 'Good job!'),
((SELECT id FROM posts ORDER BY id DESC LIMIT 1), 'Comment', 'I Like Commenting.');

INSERT INTO post_claps (post_id) VALUES
((SELECT id - 1 FROM posts ORDER BY id DESC LIMIT 1)),
((SELECT id - 1 FROM posts ORDER BY id DESC LIMIT 1)),
((SELECT id - 1 FROM posts ORDER BY id DESC LIMIT 1)),
((SELECT id - 1 FROM posts ORDER BY id DESC LIMIT 1)),
((SELECT id FROM posts ORDER BY id DESC LIMIT 1)),
((SELECT id FROM posts ORDER BY id DESC LIMIT 1));

-- You can't insert using select with multiple columns, so each column has to be grabbed individually.
INSERT INTO comment_claps (post_id, comment_id) VALUES
((SELECT post_id FROM comments ORDER BY id DESC LIMIT 1),
 (SELECT id FROM comments ORDER BY id DESC LIMIT 1)),
((SELECT post_id FROM comments ORDER BY id DESC LIMIT 1),
 (SELECT id FROM comments ORDER BY id DESC LIMIT 1)),
((SELECT post_id FROM comments ORDER BY id LIMIT 1),
 (SELECT id FROM comments ORDER BY id LIMIT 1));
