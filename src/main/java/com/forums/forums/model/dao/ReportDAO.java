package com.forums.forums.model.dao;

import com.forums.forums.model.mo.*;

import java.util.List;

public interface ReportDAO {
    public Report create(
            String content,
            User author,
            User reportedUser
    );
    public void update(Report report);
    public void delete(Report report);
    public List<Report> getAll();
}
