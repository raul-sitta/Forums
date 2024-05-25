package com.forums.forums.model.dao;

import com.forums.forums.model.mo.Post;
import com.forums.forums.model.mo.User;

public interface PostDAO {
    public Post create(
            String content,
            User author,
            Thread thread
    );
    public void update(Post post);
    public void delete(Post post);
}
