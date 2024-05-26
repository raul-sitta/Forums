package com.forums.forums.model.dao.mySQLJDBCImpl;

import java.sql.*;

import com.forums.forums.model.mo.Topic;
import com.forums.forums.model.dao.TopicDAO;

import com.forums.forums.model.mo.Category;
import com.forums.forums.model.mo.User;

public class TopicDAOMySQLJDBCImpl implements TopicDAO {

    Connection conn;

    public TopicDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    public Topic create(
            String title,
            User author,
            Category category,
            Boolean anonymous
    ) {
        // Ottengo il timestamp corrente
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        PreparedStatement ps;
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setCreationTimestamp(currentTimestamp);
        topic.setAuthor(author);
        topic.setCategory(category);
        topic.setAnonymous(anonymous);
        topic.setDeleted(false);

        try{
            String sql
                    = "INSERT INTO TOPIC "
                    + "(title,"
                    + "creationTimestamp,"
                    + "authorID,"
                    + "categoryID,"
                    + "anonymous,"
                    + "deleted) "
                    + "VALUES (?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, topic.getTitle());
            ps.setTimestamp(i++, topic.getCreationTimestamp());
            ps.setLong(i++, topic.getAuthor().getUserID());
            ps.setLong(i++, topic.getCategory().getCategoryID());
            ps.setString(i++, topic.getAnonymous() ? "Y" : "N");
            ps.setString(i++, "N");

            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return topic;
    }
    public void update(Topic topic) {

        PreparedStatement ps;

        try{
            String sql
                    = "UPDATE TOPIC "
                    + "SET "
                    + "title = ?, "
                    + "category = ? "
                    + "WHERE topicID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, topic.getTitle());
            ps.setLong(2, topic.getCategory().getCategoryID());

            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public void delete(Topic topic) {
        PreparedStatement ps;
        String sql;

        try {
            sql
                    = "UPDATE TOPIC SET "
                    + "deleted = ? "
                    + "WHERE topicID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Y");
            ps.setLong(2, topic.getTopicID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Topic read(ResultSet rs) {

        Topic topic = new Topic();

        User author = new User();
        Category category = new Category();

        topic.setAuthor(author);
        topic.setCategory(category);

        try {

            topic.setTopicID(rs.getLong("topicID"));

            topic.setTitle(rs.getString("title"));

            topic.setCreationTimestamp(rs.getTimestamp("creationTimestamp"));

            topic.getAuthor().setUserID(rs.getLong("authorID"));

            topic.getCategory().setCategoryID(rs.getLong("categoryID"));

            topic.setAnonymous(rs.getBoolean("anonymous"));

            topic.setDeleted(rs.getString("deleted").equals("Y"));

        } catch (SQLException sqle) {

            throw new RuntimeException("Error: read rs - Topic", sqle);

        }

        return topic;
    }
}
