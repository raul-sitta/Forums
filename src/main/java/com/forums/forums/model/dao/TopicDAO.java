package com.forums.forums.model.dao;

import com.forums.forums.model.mo.*;

import java.sql.*;
import java.util.List;

public interface TopicDAO {
    public Topic create(
            String title,
            Timestamp creationTimestamp,
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
    public List<Topic> findByParameters(
            Long pageIndex,
            TopicSearchFilter topicSearchFilter);
    public Long countPagesByParameters(
            TopicSearchFilter topicSearchFilter);
    public Topic findByTopicIDWithPosts(Long pageIndex, Long topicID);
    public List<Topic> getAll();
}
