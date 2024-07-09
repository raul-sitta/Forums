package com.forums.forums.model.dao.mySQLJDBCImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.forums.forums.model.mo.Topic;
import com.forums.forums.model.dao.TopicDAO;

import com.forums.forums.model.mo.Category;
import com.forums.forums.model.mo.User;

public class TopicDAOMySQLJDBCImpl implements TopicDAO {

    private final String COUNTER_ID = "topicID";
    private static final long ITEMS_PER_PAGE = 10L;
    Connection conn;

    public TopicDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
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
                    + "title,"
                    + "creationTimestamp,"
                    + "authorID,"
                    + "categoryID,"
                    + "anonymous,"
                    + "deleted) "
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
                    + "title = ?, "
                    + "category = ? "
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

            if (category != null) sql += "WHERE categoryID = ? ";


            String orderBy = sortNewestFirst ? "DESC " : "ASC ";

            sql += " ORDER BY creationTimestamp " + orderBy;

            if (index != null) sql += "LIMIT ?, ? ";

            ps = conn.prepareStatement(sql);

            int i = 1;
            if (category != null) ps.setLong(i++, category.getCategoryID());
            if (index!=null) {
                ps.setLong(i++, index * ITEMS_PER_PAGE);
                ps.setLong(i++, ITEMS_PER_PAGE);
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
            Boolean sortNewestFirst,
            String title,
            String authorName,
            String categoryName,
            Date moreRecentThan,
            Date olderThan,
            Boolean isAnonymous) {

        // Controllo degli argomenti
        if (pageIndex != null && pageIndex < 0) {
            throw new IllegalArgumentException("Errore: il parametro pageIndex non può essere negativo");
        }

        if (sortNewestFirst == null) {
            throw new IllegalArgumentException("Errore: il parametro sortNewestFirst non può essere null");
        }

        PreparedStatement ps;

        List<Topic> topics = new ArrayList<>();

        try {
            String sql = "SELECT * FROM TOPIC AS T " +
                         "LEFT JOIN USER AS U ON T.authorID = U.userID " +
                         "LEFT JOIN CATEGORY AS C ON T.categoryID = C.categoryID ";
            String whereClause = "";

            // Costruzione dinamica della query
            if (title != null && !title.trim().isEmpty()) {
                whereClause += addCondition(whereClause, "T.title LIKE ?");
            }
            if (authorName != null && !authorName.trim().isEmpty()) {
                whereClause += addCondition(whereClause, "U.username LIKE ?");
            }
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                whereClause += addCondition(whereClause, "C.name LIKE ?");
            }
            if (moreRecentThan != null) {
                whereClause += addCondition(whereClause, "T.creationTimestamp > ?");
            }
            if (olderThan != null) {
                whereClause += addCondition(whereClause, "T.creationTimestamp < ?");
            }
            if (isAnonymous != null) {
                whereClause += addCondition(whereClause, "T.anonymous = ?");
            }

            if (!whereClause.isEmpty()) {
                sql += "WHERE " + whereClause + " ";
            }

            String orderBy = sortNewestFirst ? "DESC" : "ASC";
            sql += "ORDER BY T.creationTimestamp " + orderBy + " ";

            if (pageIndex != null) {
                sql += "LIMIT ? OFFSET ?";
            }

            ps = conn.prepareStatement(sql);

            int i = 1;

            if (title != null && !title.trim().isEmpty()) {
                ps.setString(i++, "%" + title + "%");
            }
            if (authorName != null && !authorName.trim().isEmpty()) {
                ps.setString(i++, "%" + authorName + "%");
            }
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                ps.setString(i++, "%" + categoryName + "%");
            }
            if (moreRecentThan != null) {
                ps.setDate(i++, new java.sql.Date(moreRecentThan.getTime()));
            }
            if (olderThan != null) {
                ps.setDate(i++, new java.sql.Date(olderThan.getTime()));
            }
            if (isAnonymous != null) {
                ps.setString(i++, isAnonymous ? "Y" : "N");
            }
            if (pageIndex != null) {
                ps.setLong(i++, ITEMS_PER_PAGE); // Limit
                ps.setLong(i++, pageIndex * ITEMS_PER_PAGE); // Offset
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

    public Long countPagesByCategory (Category category) {
        PreparedStatement ps;
        long pageCount = 0L;

        try {
            String sql = "SELECT COUNT(*) FROM TOPIC ";

            if (category != null) {
                sql += "WHERE categoryID = ? ";
            }

            ps = conn.prepareStatement(sql);

            int i = 1;
            if (category != null) {
                ps.setLong(i++, category.getCategoryID());
            }

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

    private String addCondition(String whereClause, String condition) {
        if (!whereClause.isEmpty()) {
            return " AND " + condition;
        }
        return condition;
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
