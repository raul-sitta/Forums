package com.forums.forums.model.dao.mySQLJDBCImpl;

import com.forums.forums.model.dao.PostDAO;
import com.forums.forums.model.mo.Post;
import com.forums.forums.model.mo.User;
import com.forums.forums.model.mo.Topic;
import java.sql.*;

public class PostDAOMySQLJDBCImpl implements PostDAO {
    Connection conn;

    public PostDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    public Post create(String content, User author, Topic topic) {
        // Ottengo il timestamp corrente
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Post post = new Post();
        post.setContent(content);
        post.setCreationTimestamp(currentTimestamp);
        post.setAuthor(author);
        post.setTopic(topic);
        post.setDeleted(false);

        PreparedStatement ps;
        try {
            String sql = "INSERT INTO POST "
                    + "(content, "
                    + "creationTimestamp, "
                    + "authorID, "
                    + "topicID, "
                    + "deleted) "
                    + "VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, post.getContent());
            ps.setTimestamp(i++, post.getCreationTimestamp());
            ps.setLong(i++, post.getAuthor().getUserID());
            ps.setLong(i++, post.getTopic().getTopicID());
            ps.setString(i++,"N");

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return post;
    }

    public void update(Post post) {
        PreparedStatement ps;
        try {
            String sql = "UPDATE POST SET "
                    + "content = ?, "
                    + "WHERE postID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, post.getContent());
            ps.setLong(2, post.getPostID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Post post) {
        PreparedStatement ps;
        String sql;

        try {
            sql
                    = "UPDATE POST SET "
                    + "deleted = ? "
                    + "WHERE postID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Y");
            ps.setLong(2, post.getPostID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Post read(ResultSet rs) {
        Post post = new Post();
        User author = new User();
        Topic topic = new Topic();

        post.setAuthor(author);
        post.setTopic(topic);

        try {
            post.setPostID(rs.getLong("postID"));
            post.setContent(rs.getString("content"));
            post.setCreationTimestamp(rs.getTimestamp("creationTimestamp"));
            post.getAuthor().setUserID(rs.getLong("authorID"));
            post.getTopic().setTopicID(rs.getLong("topicID"));
            post.setDeleted(rs.getString("deleted").equals("Y"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        return post;
    }


}
