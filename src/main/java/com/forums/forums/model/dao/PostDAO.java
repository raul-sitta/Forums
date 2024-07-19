package com.forums.forums.model.dao;

import com.forums.forums.model.mo.*;

import java.sql.Timestamp;
import java.util.List;

public interface PostDAO {
    public Post create(
            String content,
            Timestamp creationTimestamp,
            User author,
            Topic Topic
    );
    public void update(Post post);
    public void delete(Post post);
    public List<Post> findByTopic
            (Long pageIndex, Topic topic);
    public Post findByID(Long postID);
    public List<Post> getAll();
}
