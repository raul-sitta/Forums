package com.forums.forums.model.dao.mySQLJDBCImpl;

import com.forums.forums.model.dao.PostDAO;
import com.forums.forums.model.mo.Post;
import com.forums.forums.model.mo.User;
import com.forums.forums.model.mo.Topic;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAOMySQLJDBCImpl implements PostDAO {

    private final String COUNTER_ID = "postID";
    Connection conn;

    public PostDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Post create(String content, User author, Topic topic, Post parentPost) {
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
            String sql = "UPDATE COUNTER SET counterValue=counterValue+1 where counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            sql = "SELECT counterValue FROM COUNTER WHERE counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();

            post.setPostID(resultSet.getLong("counterValue"));

            resultSet.close();

            sql
                    = "INSERT INTO POST "
                    + "(postID, "
                    + "content, "
                    + "creationTimestamp, "
                    + "authorID, "
                    + "topicID, "
                    + "parentPostID, "
                    + "deleted) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setLong(i++, post.getPostID());
            ps.setString(i++, post.getContent());
            ps.setTimestamp(i++, post.getCreationTimestamp());
            ps.setLong(i++, post.getAuthor().getUserID());
            ps.setLong(i++, post.getTopic().getTopicID());
            ps.setLong(i++, post.getParentPost().getPostID());
            ps.setString(i++,"N");

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return post;
    }

    @Override
    public void update(Post post) {
        PreparedStatement ps;
        try {
            String sql = "UPDATE POST SET "
                    + "content = ?, "
                    + "WHERE postID = ?";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, post.getContent());
            ps.setLong(i++, post.getPostID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Post post) {
        PreparedStatement ps;
        String sql;

        try {
            sql
                    = "UPDATE POST SET "
                    + "deleted = ? "
                    + "WHERE postID = ?";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, "Y");
            ps.setLong(i++, post.getPostID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> getAll() {
        PreparedStatement ps;
        List<Post> posts = new ArrayList<>();

        try {
            String sql = "SELECT * FROM POST";

            ps = conn.prepareStatement(sql);

            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()){
                Post post = read(resultSet);
                posts.add(post);
            }
            resultSet.close();
            ps.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return posts;
    }

    Post read(ResultSet rs) {
        Post post = new Post();
        User author = new User();
        Topic topic = new Topic();
        Post parentPost = new Post();

        post.setAuthor(author);
        post.setTopic(topic);

        try {
            post.setPostID(rs.getLong("postID"));
            post.setContent(rs.getString("content"));
            post.setCreationTimestamp(rs.getTimestamp("creationTimestamp"));
            post.getAuthor().setUserID(rs.getLong("authorID"));
            post.getTopic().setTopicID(rs.getLong("topicID"));
            post.getParentPost().setPostID(rs.getLong("parentPostID"));
            post.setDeleted(rs.getString("deleted").equals("Y"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        return post;
    }

}
