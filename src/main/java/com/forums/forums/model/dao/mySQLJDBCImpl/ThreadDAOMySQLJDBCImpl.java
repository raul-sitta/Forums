package com.forums.forums.model.dao.mySQLJDBCImpl;

import java.sql.*;

import com.forums.forums.model.dao.exception.DuplicatedObjectException;
import com.forums.forums.model.mo.Thread;
import com.forums.forums.model.dao.ThreadDAO;

import com.forums.forums.model.mo.Category;
import com.forums.forums.model.mo.User;

public class ThreadDAOMySQLJDBCImpl implements ThreadDAO {

    Connection conn;

    public ThreadDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    public Thread create(
            String title,
            User author,
            Category category,
            Boolean anonymous
    ) {
        // Ottengo il timestamp corrente
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        PreparedStatement ps;
        Thread thread = new Thread();
        thread.setTitle(title);
        thread.setCreationTimestamp(currentTimestamp);
        thread.setAuthor(author);
        thread.setCategory(category);
        thread.setAnonymous(anonymous);
        thread.setDeleted(false);

        try{
            String sql
                    = "INSERT INTO THREAD "
                    + "(title,"
                    + "creationTimestamp,"
                    + "authorID,"
                    + "categoryID,"
                    + "anonymous,"
                    + "deleted) "
                    + "VALUES (?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, thread.getTitle());
            ps.setTimestamp(i++, thread.getCreationTimestamp());
            ps.setLong(i++, thread.getAuthor().getUserID());
            ps.setLong(i++, thread.getCategory().getCategoryID());
            ps.setString(i++, thread.getAnonymous() ? "Y" : "N");
            ps.setString(i++, "N");

            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return thread;
    }
    public void update(Thread thread) {

        PreparedStatement ps;

        try{
            String sql
                    = "UPDATE THREAD "
                    + "SET "
                    + "title = ?, "
                    + "category = ? "
                    + "WHERE threadID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, thread.getTitle());
            ps.setLong(2, thread.getCategory().getCategoryID());

            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public void delete(Thread thread) {
        PreparedStatement ps;
        String sql;

        try {
            sql
                    = "UPDATE THREAD SET "
                    + "deleted = ? "
                    + "WHERE threadID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Y");
            ps.setLong(2, thread.getThreadID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Thread read(ResultSet rs) {

        Thread thread = new Thread();

        User author = new User();
        Category category = new Category();

        thread.setAuthor(author);
        thread.setCategory(category);

        try {

            thread.setThreadID(rs.getLong("threadID"));

            thread.setTitle(rs.getString("title"));

            thread.setCreationTimestamp(rs.getTimestamp("creationTimestamp"));

            thread.getAuthor().setUserID(rs.getLong("authorID"));

            thread.getCategory().setCategoryID(rs.getLong("categoryID"));

            thread.setAnonymous(rs.getBoolean("anonymous"));

            thread.setDeleted(rs.getString("deleted").equals("Y"));

        } catch (SQLException sqle) {

            throw new RuntimeException("Error: read rs - Thread", sqle);

        }

        return thread;
    }
}
