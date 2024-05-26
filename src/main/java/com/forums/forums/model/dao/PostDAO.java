package com.forums.forums.model.dao;

import com.forums.forums.model.mo.Post;
import com.forums.forums.model.mo.User;
import com.forums.forums.model.mo.Topic;

public interface PostDAO {
    public Post create(
            String content,
            User author,
            Topic Topic
    );
    public void update(Post post);
    public void delete(Post post);
}
