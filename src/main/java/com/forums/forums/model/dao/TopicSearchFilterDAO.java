package com.forums.forums.model.dao;

import com.forums.forums.model.mo.*;

import java.sql.*;
import java.util.List;

public interface TopicSearchFilterDAO {
    public TopicSearchFilter create(
            String title,
            String authorName,
            String categoryName,
            Timestamp moreRecentThan,
            Timestamp olderThan,
            Boolean isAnonymous,
            Boolean sortNewestFirst
    );
    public void update(TopicSearchFilter topicSearchFilter);
    public void delete(TopicSearchFilter topicSearchFilter);
    public TopicSearchFilter findTopicSearchFilter();
}
