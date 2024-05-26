package com.forums.forums.model.dao.mySQLJDBCImpl;

import com.forums.forums.model.dao.ReportDAO;
import com.forums.forums.model.mo.Report;
import com.forums.forums.model.mo.User;
import com.forums.forums.model.mo.Post;
import com.forums.forums.model.mo.Topic;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAOMySQLJDBCImpl implements ReportDAO {
    Connection conn;

    public ReportDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Report create(
            String content,
            User author,
            Post reportedPost,
            Topic reportedTopic,
            User reportedUser
    ) {
        // Ottengo il timestamp corrente
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        PreparedStatement ps;
        Report report = new Report();
        report.setContent(content);
        report.setCreationTimestamp(currentTimestamp);
        report.setAuthor(author);
        report.setReportedPost(reportedPost);
        report.setReportedTopic(reportedTopic);
        report.setReportedUser(reportedUser);

        try {
            String sql = "INSERT INTO REPORT "
                    + "(content, "
                    + "creationTimestamp, "
                    + "authorID, "
                    + "reportedPostID, "
                    + "reportedTopicID, "
                    + "reportedUserID) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, report.getContent());
            ps.setTimestamp(i++, report.getCreationTimestamp());
            ps.setLong(i++, report.getAuthor().getUserID());
            ps.setLong(i++, report.getReportedPost().getPostID());
            ps.setLong(i++, report.getReportedTopic().getTopicID());
            ps.setLong(i++, report.getReportedUser().getUserID());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return report;
    }

    @Override
    public void update(Report report) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Report report) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Report> getAll() {
        PreparedStatement ps;
        List<Report> reports = new ArrayList<>();

        try {
            String sql = "SELECT * FROM REPORT";

            ps = conn.prepareStatement(sql);

            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()){
                Report report = read(resultSet);
                reports.add(report);
            }
            resultSet.close();
            ps.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return reports;
    }

    public Report read(ResultSet rs) {
        Report report = new Report();
        User author = new User();
        Post reportedPost = new Post();
        Topic reportedTopic = new Topic();
        User reportedUser = new User();

        report.setAuthor(author);
        report.setReportedPost(reportedPost);
        report.setReportedTopic(reportedTopic);
        report.setReportedUser(reportedUser);

        try {
            report.setReportID(rs.getLong("reportID"));
            report.setContent(rs.getString("content"));
            report.getAuthor().setUserID(rs.getLong("authorID"));
            report.getReportedPost().setPostID(rs.getLong("reportedPostID"));
            report.getReportedTopic().setTopicID(rs.getLong("reportedTopicID"));
            report.getReportedUser().setUserID(rs.getLong("reportedUserID"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Report", sqle);
        }

        return report;
    }
}
