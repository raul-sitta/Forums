package com.forums.forums.model.dao;

import com.forums.forums.model.mo.*;

import java.util.List;

public interface PostDAO {
    public Post create(
            String content,
            User author,
            Topic Topic,
            Post parentPost
    );
    public void update(Post post);
    public void delete(Post post);
    public List<Post> getAll();
}
