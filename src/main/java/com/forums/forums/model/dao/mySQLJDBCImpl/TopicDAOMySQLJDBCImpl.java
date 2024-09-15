package com.forums.forums.model.dao.mySQLJDBCImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.forums.forums.model.dao.*;
import com.forums.forums.model.mo.*;
import com.forums.forums.services.config.Configuration;

public class TopicDAOMySQLJDBCImpl implements TopicDAO {

    private final String COUNTER_ID = "topicID";
    private static final long TOPICS_PER_PAGE = 10L;
    private static final long POSTS_PER_PAGE = 10L;
    Connection conn;

    public TopicDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Topic create(
            String title,
            Timestamp creationTimestamp,
            User author,
            Category category,
            Boolean anonymous
    ) {

        PreparedStatement ps;
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setCreationTimestamp(creationTimestamp);
        topic.setAuthor(author);
        topic.setCategory(category);
        topic.setAnonymous(anonymous);
        topic.setDeleted(false);

        try{
            String sql = "UPDATE COUNTER SET counterValue=counterValue+1 where counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            sql = "SELECT counterValue FROM COUNTER WHERE counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();

            topic.setTopicID(resultSet.getLong("counterValue"));

            resultSet.close();

            sql
                    = "INSERT INTO TOPIC "
                    + "(topicID,"
                    + "topicTitle,"
                    + "topicCreationTimestamp,"
                    + "topicAuthorID,"
                    + "topicCategoryID,"
                    + "topicAnonymous,"
                    + "topicDeleted) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setLong(i++, topic.getTopicID());
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

    @Override
    public void update(Topic topic) {

        PreparedStatement ps;

        try{
            String sql
                    = "UPDATE TOPIC "
                    + "SET "
                    + "topicTitle = ?, "
                    + "topicCategoryID = ? "
                    + "WHERE topicID = ?";
            ps = conn.prepareStatement(sql);

            int i=1;
            ps.setString(i++, topic.getTitle());
            ps.setLong(i++, topic.getCategory().getCategoryID());
            ps.setLong(i++, topic.getTopicID());

            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Topic topic) {
        PreparedStatement ps;
        String sql;

        try {
            sql
                    = "UPDATE TOPIC SET "
                    + "topicDeleted = ? "
                    + "WHERE topicID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Y");
            ps.setLong(2, topic.getTopicID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Topic> findInTimeRangeByCategory
            (Category category,
             Long index,
             Boolean sortNewestFirst) {

        //Controllo gli argomenti

        if (index != null && index < 0) {
            throw new IllegalArgumentException("Errore: il parametro index non può essere negativo");
        }

        if (sortNewestFirst == null) {
            throw new IllegalArgumentException("Errore: il parametro sortNewestFirst non può essere null");
        }

        PreparedStatement ps;

        List<Topic> topics = new ArrayList<>();

        try {
            String sql = "SELECT * FROM TOPIC ";

            if (category != null) sql += "WHERE topicCategoryID = ? ";


            String orderBy = sortNewestFirst ? "DESC " : "ASC ";

            sql += " ORDER BY topicCreationTimestamp " + orderBy;

            if (index != null) sql += "LIMIT ?, ? ";

            ps = conn.prepareStatement(sql);

            int i = 1;
            if (category != null) ps.setLong(i++, category.getCategoryID());
            if (index!=null) {
                ps.setLong(i++, index * TOPICS_PER_PAGE);
                ps.setLong(i++, TOPICS_PER_PAGE);
            }

            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()){
                Topic topic = read(resultSet);
                topics.add(topic);
            }
            resultSet.close();
            ps.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return topics;
    }

    @Override
    public List<Topic> findByParameters(
            Long pageIndex,
            TopicSearchFilter topicSearchFilter) {

        // Controllo degli argomenti
        if (pageIndex != null && pageIndex < 1) {
            throw new IllegalArgumentException("Errore: il parametro pageIndex non può essere minore di 1");
        }

        if (topicSearchFilter.getSortNewestFirst() == null) {
            throw new IllegalArgumentException("Errore: il parametro sortNewestFirst non può essere null");
        }

        PreparedStatement ps;

        List<Topic> topics = new ArrayList<>();

        try {
            String sql = "SELECT * FROM TOPIC AS T " +
                         "LEFT JOIN USER AS U ON T.topicAuthorID = U.userID " +
                         "LEFT JOIN CATEGORY AS C ON T.topicCategoryID = C.categoryID ";
            String whereClause = "T.topicDeleted = 'N'";

            // Costruzione dinamica della query
            if (topicSearchFilter.getTitle() != null) {
                whereClause += addCondition(whereClause, "T.topicTitle LIKE ?");
            }
            if (topicSearchFilter.getAuthorName() != null) {
                whereClause += addCondition(whereClause, "U.userUsername LIKE ?");
            }
            if (topicSearchFilter.getCategoryName() != null) {
                whereClause += addCondition(whereClause, "C.categoryName = ?");
            }
            if (topicSearchFilter.getMoreRecentThan() != null) {
                whereClause += addCondition(whereClause, "T.topicCreationTimestamp > ?");
            }
            if (topicSearchFilter.getOlderThan() != null) {
                whereClause += addCondition(whereClause, "T.topicCreationTimestamp < ?");
            }
            if (topicSearchFilter.getAnonymous() != null) {
                whereClause += addCondition(whereClause, "T.topicAnonymous = ?");
            }

            if (!whereClause.isEmpty()) {
                sql += "WHERE " + whereClause + " ";
            }

            String orderBy = topicSearchFilter.getSortNewestFirst() ? "DESC" : "ASC";
            sql += "ORDER BY T.topicCreationTimestamp " + orderBy + " ";

            if (pageIndex != null) {
                sql += "LIMIT ? OFFSET ?";
            }

            ps = conn.prepareStatement(sql);

            int i = 1;

            if (topicSearchFilter.getTitle() != null) {
                ps.setString(i++, "%" + topicSearchFilter.getTitle() + "%");
            }
            if (topicSearchFilter.getAuthorName() != null) {
                ps.setString(i++, "%" + topicSearchFilter.getAuthorName() + "%");
            }
            if (topicSearchFilter.getCategoryName() != null) {
                ps.setString(i++, topicSearchFilter.getCategoryName());
            }
            if (topicSearchFilter.getMoreRecentThan() != null) {
                ps.setTimestamp(i++, topicSearchFilter.getMoreRecentThan());
            }
            if (topicSearchFilter.getOlderThan() != null) {
                ps.setTimestamp(i++, topicSearchFilter.getOlderThan());
            }
            if (topicSearchFilter.getAnonymous() != null) {
                ps.setString(i++, topicSearchFilter.getAnonymous() ? "Y" : "N");
            }
            if (pageIndex != null) {
                ps.setLong(i++, TOPICS_PER_PAGE);
                ps.setLong(i++, (pageIndex - 1) * TOPICS_PER_PAGE);
            }

            ResultSet resultSet = ps.executeQuery();

            //Creo i DAOMySQLJDBCImpl per leggere il resultSet usando i metodi di altri DAO
            UserDAOMySQLJDBCImpl userDAOMySQLJDBC = new UserDAOMySQLJDBCImpl(this.conn);
            CategoryDAOMySQLJDBCImpl categoryDAOMySQLJDBC = new CategoryDAOMySQLJDBCImpl(this.conn);

            while (resultSet.next()) {
                Topic topic = read(resultSet);
                User author = userDAOMySQLJDBC.read(resultSet);

                Category category = categoryDAOMySQLJDBC.read(resultSet);
                topic.setAuthor(author);
                topic.setCategory(category);
                topics.add(topic);
            }

            resultSet.close();

            ps.close();

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return topics;
    }

    private String addCondition(String whereClause, String condition) {
        if (!whereClause.isEmpty()) {
            return " AND " + condition;
        }
        return condition;
    }

    @Override
    public Long countTopicPagesByParameters(
            TopicSearchFilter topicSearchFilter) {

        PreparedStatement ps;

        Long topicsPageCount = 0L;

        try {
            String sql = "SELECT COUNT(*) AS total FROM TOPIC AS T " +
                    "LEFT JOIN USER AS U ON T.topicAuthorID = U.userID " +
                    "LEFT JOIN CATEGORY AS C ON T.topicCategoryID = C.categoryID ";
            String whereClause = "T.topicDeleted = 'N'";

            // Costruzione dinamica della query
            if (topicSearchFilter.getTitle() != null) {
                whereClause += addCondition(whereClause, "T.topicTitle LIKE ?");
            }
            if (topicSearchFilter.getAuthorName() != null) {
                whereClause += addCondition(whereClause, "U.userUsername LIKE ?");
            }
            if (topicSearchFilter.getCategoryName() != null) {
                whereClause += addCondition(whereClause, "C.categoryName = ?");
            }
            if (topicSearchFilter.getMoreRecentThan() != null) {
                whereClause += addCondition(whereClause, "T.topicCreationTimestamp > ?");
            }
            if (topicSearchFilter.getOlderThan() != null) {
                whereClause += addCondition(whereClause, "T.topicCreationTimestamp < ?");
            }
            if (topicSearchFilter.getAnonymous() != null) {
                whereClause += addCondition(whereClause, "T.topicAnonymous = ?");
            }

            if (!whereClause.isEmpty()) {
                sql += "WHERE " + whereClause + " ";
            }

            ps = conn.prepareStatement(sql);

            int i = 1;

            if (topicSearchFilter.getTitle() != null) {
                ps.setString(i++, "%" + topicSearchFilter.getTitle() + "%");
            }
            if (topicSearchFilter.getAuthorName() != null) {
                ps.setString(i++, "%" + topicSearchFilter.getAuthorName() + "%");
            }
            if (topicSearchFilter.getCategoryName() != null) {
                ps.setString(i++, topicSearchFilter.getCategoryName());
            }
            if (topicSearchFilter.getMoreRecentThan() != null) {
                ps.setTimestamp(i++, topicSearchFilter.getMoreRecentThan());
            }
            if (topicSearchFilter.getOlderThan() != null) {
                ps.setTimestamp(i++, topicSearchFilter.getOlderThan());
            }
            if (topicSearchFilter.getAnonymous() != null) {
                ps.setString(i++, topicSearchFilter.getAnonymous() ? "Y" : "N");
            }

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                long totalItems = resultSet.getLong("total");
                topicsPageCount = (totalItems + TOPICS_PER_PAGE - 1) / TOPICS_PER_PAGE;
            }

            resultSet.close();

            ps.close();

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return topicsPageCount;
    }

    @Override
    public Topic findByIDWithPosts(Long pageIndex, Long topicID) {
        if (topicID == null) {
            throw new IllegalArgumentException("Errore: il parametro topicID non può essere null");
        }
        if (pageIndex != null && pageIndex < 1) {
            throw new IllegalArgumentException("Errore: il parametro pageIndex non può essere minore di 1");
        }

        Topic topic = null;
        List<Post> posts = null;
        PreparedStatement ps;

        try {
            String sql = "SELECT " +
                    "T.topicID, " +
                    "T.topicTitle, " +
                    "T.topicCreationTimestamp, " +
                    "T.topicAuthorID, " +
                    "T.topicCategoryID, " +
                    "T.topicAnonymous, " +
                    "T.topicDeleted, " +
                    "P.postID, " +
                    "P.postContent, " +
                    "P.postCreationTimestamp, " +
                    "P.postAuthorID, " +
                    "P.postTopicID, " +
                    "P.postDeleted, " +
                    "P.postEdited, " +
                    "U.userID, " +
                    "U.userUsername, " +
                    "U.userPassword, " +
                    "U.userFirstname, " +
                    "U.userSurname, " +
                    "U.userEmail, " +
                    "U.userBirthDate, " +
                    "U.userRegistrationTimestamp, " +
                    "U.userRole, " +
                    "U.userProfilePicPath, " +
                    "U.userDeleted, " +
                    "COUNT(M.mediaID) AS mediaCount " +
                    "FROM TOPIC AS T " +
                    "LEFT JOIN POST AS P ON T.topicID = P.postTopicID AND (P.postDeleted = 'N' OR P.postDeleted IS NULL) " +
                    "LEFT JOIN USER AS U ON U.userID = P.postAuthorID " +
                    "LEFT JOIN MEDIA AS M ON P.postID = M.mediaPostID " +
                    "WHERE " +
                    "T.topicID = ? AND " +
                    "T.topicDeleted = 'N' " +
                    "GROUP BY " +
                    "T.topicID, " +
                    "T.topicTitle, " +
                    "T.topicCreationTimestamp, " +
                    "T.topicAuthorID, " +
                    "T.topicCategoryID, " +
                    "T.topicAnonymous, " +
                    "T.topicDeleted, " +
                    "P.postID, " +
                    "P.postContent, " +
                    "P.postCreationTimestamp, " +
                    "P.postAuthorID, " +
                    "P.postTopicID, " +
                    "P.postDeleted, " +
                    "P.postEdited, " +
                    "U.userID, " +
                    "U.userUsername, " +
                    "U.userPassword, " +
                    "U.userFirstname, " +
                    "U.userSurname, " +
                    "U.userEmail, " +
                    "U.userBirthDate, " +
                    "U.userRegistrationTimestamp, " +
                    "U.userRole, " +
                    "U.userProfilePicPath, " +
                    "U.userDeleted " +
                    "ORDER BY " +
                    "P.postCreationTimestamp ASC ";

            if (pageIndex != null) {
                sql += "LIMIT ? OFFSET ? ";
            }

            ps = conn.prepareStatement(sql);
            int i = 1;
            ps.setLong(i++, topicID);
            if  (pageIndex != null) {
                ps.setLong(i++, POSTS_PER_PAGE);
                ps.setLong(i++, (pageIndex - 1) * POSTS_PER_PAGE);
            }

            ResultSet resultSet = ps.executeQuery();

            //Creo i DAOMySQLJDBCImpl per leggere il resultSet usando i metodi di altri DAO
            PostDAOMySQLJDBCImpl postDAOMySQLJDBC = new PostDAOMySQLJDBCImpl(this.conn);
            UserDAOMySQLJDBCImpl userDAOMySQLJDBC = new UserDAOMySQLJDBCImpl(this.conn);
            while (resultSet.next()) {

                topic = read(resultSet);

                if (resultSet.getObject("postID", Long.class) != null) {

                    if (posts == null) posts = new ArrayList<>();

                    Post post = postDAOMySQLJDBC.read(resultSet);

                    if (resultSet.getLong("mediaCount") > 0) {
                        List<Media> medias = new ArrayList<>();
                        post.setMedias(medias);
                    }

                    User author = userDAOMySQLJDBC.read(resultSet);

                    post.setAuthor(author);
                    posts.add(post);

                }
            }

            if (posts!=null) topic.setPosts(posts);

            resultSet.close();

            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'esecuzione della query", e);
        }

        return topic;
    }

    @Override
    public Long countPostPagesByTopicID (Long topicID) {
        PreparedStatement ps;

        Long postsPageCount = 0L;

        try {
            String sql = "SELECT COUNT(*) AS total "
                    + "FROM TOPIC AS T "
                    + "LEFT JOIN POST AS P ON T.topicID = P.postTopicID AND (P.postDeleted = 'N' OR P.postDeleted IS NULL) "
                    + "WHERE T.topicID = ? ";


            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setLong(i++, topicID);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                long totalItems = resultSet.getLong("total");
                postsPageCount = (totalItems + POSTS_PER_PAGE - 1) / POSTS_PER_PAGE;
            }

            resultSet.close();

            ps.close();

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return postsPageCount;
    }

    @Override
    public Topic findByID(Long topicID) {
        if (topicID == null) {
            throw new IllegalArgumentException("Errore: il parametro topicID non può essere null");
        }

        Topic topic = null;
        PreparedStatement ps;

        try {

            String sql = " SELECT T.*, U.*, C.* "
                    + " FROM TOPIC T "
                    + " JOIN USER U ON T.topicAuthorID = U.userID "
                    + " JOIN CATEGORY C ON T.topicCategoryID = C.categoryID "
                    + " WHERE T.topicID = ? ";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, topicID);

            ResultSet resultSet = ps.executeQuery();

            //Creo i DAOMySQLJDBCImpl per leggere il resultSet usando i metodi di altri DAO
            UserDAOMySQLJDBCImpl userDAOMySQLJDBC = new UserDAOMySQLJDBCImpl(this.conn);
            CategoryDAOMySQLJDBCImpl categoryDAOMySQLJDBC = new CategoryDAOMySQLJDBCImpl(this.conn);
            if (resultSet.next()) {
                User author = userDAOMySQLJDBC.read(resultSet);
                Category category = categoryDAOMySQLJDBC.read(resultSet);
                topic = read(resultSet);
                topic.setAuthor(author);
                topic.setCategory(category);
            }
            resultSet.close();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return topic;

    }

    @Override
    public List<Topic> getAll() {
        PreparedStatement ps;
        List<Topic> topics = new ArrayList<>();

        try {
            String sql = "SELECT * FROM TOPIC";

            ps = conn.prepareStatement(sql);

            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()){
                Topic topic = read(resultSet);
                topics.add(topic);
            }
            resultSet.close();
            ps.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return topics;
    }

    Topic read(ResultSet rs) {

        Topic topic = new Topic();

        User author = new User();
        Category category = new Category();

        topic.setAuthor(author);
        topic.setCategory(category);

        try {
            topic.setTopicID(rs.getLong("topicID"));
        } catch (SQLException sqle) {}

        try {
            topic.setTitle(rs.getString("topicTitle"));
        } catch (SQLException sqle) {}

        try {
            topic.setCreationTimestamp(rs.getTimestamp("topicCreationTimestamp"));
        } catch (SQLException sqle) {}

        try {
            topic.getAuthor().setUserID(rs.getLong("topicAuthorID"));
        } catch (SQLException sqle) {}

        try {
            topic.getCategory().setCategoryID(rs.getLong("topicCategoryID"));
        } catch (SQLException sqle) {}

        try {
            topic.setAnonymous(rs.getBoolean("topicAnonymous"));
        } catch (SQLException sqle) {}

        try {
            topic.setDeleted(rs.getString("topicDeleted").equals("Y"));
        } catch (SQLException sqle) {}

        return topic;
    }
}
