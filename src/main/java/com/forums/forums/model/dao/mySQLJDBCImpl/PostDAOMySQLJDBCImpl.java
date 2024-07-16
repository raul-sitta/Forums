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
    private static final long ITEMS_PER_PAGE = 10L;
    Connection conn;

    public PostDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Post create(String content, Timestamp creationTimestamp, User author, Topic topic, Post parentPost) {
        Post post = new Post();
        post.setContent(content);
        post.setCreationTimestamp(creationTimestamp);
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
            if (post.getParentPost() != null && post.getParentPost().getPostID() != null) {
                ps.setLong(i++, post.getParentPost().getPostID());
            } else {
                ps.setNull(i++, Types.BIGINT);
            }
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
    public List<Post> findByTopic
            (Long pageIndex, Topic topic) {

        //Controllo gli argomenti

        if (pageIndex != null && pageIndex < 1) {
            throw new IllegalArgumentException("Errore: il parametro index non può essere minore di 1");
        }

        if (topic == null) {
            throw new IllegalArgumentException("Errore: il parametro topic non può essere nullo");
        }

        PreparedStatement ps;

        List<Post> posts = new ArrayList<>();

        try {
            String sql = "SELECT * FROM TOPIC WHERE topicID = ? ";

            if (pageIndex != null) sql += "LIMIT ?, ? ";

            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setLong(i++, topic.getTopicID());
            if (pageIndex!=null) {
                ps.setLong(i++, (pageIndex - 1) * ITEMS_PER_PAGE);
                ps.setLong(i++, ITEMS_PER_PAGE);
            }

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

    @Override
    public Long countPagesByTopic (Topic topic) {

        //Controllo gli argomenti
        if (topic == null) {
            throw new IllegalArgumentException("Errore: il parametro topic non può essere null");
        }

        PreparedStatement ps;
        long pageCount = 0L;

        try {
            String sql = "SELECT COUNT(*) FROM POST WHERE topicID = ?";

            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setLong(i++, topic.getTopicID());

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                long totalItems = resultSet.getLong(1);
                pageCount = (totalItems + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
            }

            resultSet.close();
            ps.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return pageCount;
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
        post.setParentPost(parentPost);

        try {
            post.setPostID(rs.getLong("postID"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        try {
            post.setContent(rs.getString("content"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        try {
            post.setCreationTimestamp(rs.getTimestamp("creationTimestamp"));
        } catch (SQLException sqle) {
                throw new RuntimeException("Error: read rs - Post", sqle);
        }

        try {
            post.getAuthor().setUserID(rs.getLong("authorID"));
        }
        catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        try {
            post.getTopic().setTopicID(rs.getLong("topicID"));
        }
        catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        try {
            post.getParentPost().setPostID(rs.getLong("parentPostID"));
        }
        catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        try {
            post.setDeleted(rs.getString("deleted").equals("Y"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        return post;
    }

    Post readParent(ResultSet rs) {
        Post post = new Post();
        User author = new User();
        Topic topic = new Topic();
        Post parentPost = new Post();

        post.setAuthor(author);
        post.setTopic(topic);
        post.setParentPost(parentPost);

        try {
            parentPost.setPostID(rs.getLong("parentPostID"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        try {
            parentPost.setContent(rs.getString("parentContent"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        try {
            parentPost.setCreationTimestamp(rs.getTimestamp("parentCreationTimestamp"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        try {
            parentPost.getAuthor().setUserID(rs.getLong("parentAuthorID"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        try {
            parentPost.getTopic().setTopicID(rs.getLong("parentTopicID"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        try {
            parentPost.setDeleted(rs.getString("parentDeleted").equals("Y"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Post", sqle);
        }

        return parentPost;
    }

}
