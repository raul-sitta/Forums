package com.forums.forums.model.dao;

import com.forums.forums.model.mo.*;

import java.util.List;

public interface TopicDAO {
    public Topic create(
            String title,
            User author,
            Category category,
            Boolean anonymous
    );
    public void update(Topic topic);
    public void delete(Topic topic);
    public List<Topic> findInTimeRangeByCategory
            (Category category,
             Long index,
             Boolean sortNewestFirst);
    public Long countPagesByCategory (Category category);
    public List<Topic> getAll();
}
