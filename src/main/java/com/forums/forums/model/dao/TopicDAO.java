package com.forums.forums.model.dao;

import com.forums.forums.model.mo.Topic;
import com.forums.forums.model.mo.User;
import com.forums.forums.model.mo.Category;

public interface TopicDAO {
    public Topic create(
            String title,
            User author,
            Category category,
            Boolean anonymous
    );
    public void update(Topic topic);
    public void delete(Topic topic);
}
