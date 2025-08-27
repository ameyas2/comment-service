use posts_application

create table comment (
  id UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
  description STRING NOT NULL,
  created_at TIMESTAMP NOT NULL default now(),
  updated_at TIMESTAMP NOT NULL default now(),
  user_id UUID NOT NULL,
  post_id UUID NOT NULL,
  FOREIGN KEY (user_id) REFERENCES posts_application.users(id),
  FOREIGN KEY (post_id) REFERENCES posts_application.post(id)
);

-- CQL queries

CREATE KEYSPACE IF NOT EXISTS posts_application WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 2};

DESCRIBE KEYSPACES

use posts_application

CREATE TABLE comments (
        id UUID PRIMARY KEY,
        created_at timestamp,
        updated_at timestamp,
        description text,
        post_id UUID
        );

select * from comments;
