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
    public Post create(String content, Timestamp creationTimestamp, User author, Topic topic) {
        Post post = new Post();
        post.setContent(content);
        post.setCreationTimestamp(creationTimestamp);
        post.setAuthor(author);
        post.setTopic(topic);
        post.setDeleted(false);
        post.setEdited(false);

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
                    + "postContent, "
                    + "postCreationTimestamp, "
                    + "postAuthorID, "
                    + "postTopicID, "
                    + "postDeleted, "
                    + "postEdited) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setLong(i++, post.getPostID());
            ps.setString(i++, post.getContent());
            ps.setTimestamp(i++, post.getCreationTimestamp());
            ps.setLong(i++, post.getAuthor().getUserID());
            ps.setLong(i++, post.getTopic().getTopicID());
            ps.setString(i++,"N");
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
        post.setEdited(true);
        try {
            String sql = "UPDATE POST SET "
                    + "postContent = ?, "
                    + "postEdited = 'Y' "
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
                    + "postDeleted = ? "
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
            String sql = "SELECT * FROM TOPIC WHERE postTopicID = ? ";

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
    public Post findByID(Long postID) {
        PreparedStatement ps;
        Post post = null;

        try {

            String sql
                    = " SELECT * "
                    + "   FROM POST "
                    + " WHERE "
                    + "   postID = ?";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, postID);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                post = read(resultSet);
            }
            resultSet.close();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return post;
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
        } catch (SQLException sqle) {
        }

        try {
            post.setContent(rs.getString("postContent"));
        } catch (SQLException sqle) {
        }

        try {
            post.setCreationTimestamp(rs.getTimestamp("postCreationTimestamp"));
        } catch (SQLException sqle) {
        }

        try {
            post.getAuthor().setUserID(rs.getLong("postAuthorID"));
        }
        catch (SQLException sqle) {
        }

        try {
            post.getTopic().setTopicID(rs.getLong("postTopicID"));
        }
        catch (SQLException sqle) {
        }

        try {
            post.setDeleted(rs.getString("postDeleted").equals("Y"));
        } catch (SQLException sqle) {
        }

        try {
            post.setEdited(rs.getString("postEdited").equals("Y"));
        } catch (SQLException sqle) {
        }

        return post;
    }
}
