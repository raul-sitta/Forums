package com.forums.forums.model.dao;

import com.forums.forums.model.mo.*;

import java.sql.Date;
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
    public List<Topic> findByParameters(
            Long pageIndex,
            Boolean sortNewestFirst,
            String title,
            String authorName,
            String categoryName,
            Date moreRecentThan,
            Date olderThan,
            Boolean isAnonymous);
    public Long countPagesByParameters(
            String title,
            String authorName,
            String categoryName,
            Date moreRecentThan,
            Date olderThan,
            Boolean isAnonymous);
    public List<Topic> getAll();
}
