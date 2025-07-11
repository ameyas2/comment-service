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