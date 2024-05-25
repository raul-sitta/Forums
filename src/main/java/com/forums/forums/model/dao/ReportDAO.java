package com.forums.forums.model.dao;

import com.forums.forums.model.mo.Report;
import com.forums.forums.model.mo.User;
import com.forums.forums.model.mo.Post;

public interface ReportDAO {
    public Report create(
            String content,
            User author,
            Post reportedPost,
            Thread reportedThread,
            User reportedUser
    );
    public void update(Report report);
    public void delete(Report report);
}
