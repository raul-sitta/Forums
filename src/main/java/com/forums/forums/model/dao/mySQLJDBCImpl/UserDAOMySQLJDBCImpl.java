package com.forums.forums.model.dao.mySQLJDBCImpl;

import java.sql.*;

import com.forums.forums.model.mo.User;
import com.forums.forums.model.mo.Thread;
import com.forums.forums.model.mo.Post;
import com.forums.forums.model.mo.Media;
import com.forums.forums.model.dao.UserDAO;

public class UserDAOMySQLJDBCImpl implements UserDAO {

    Connection conn;

    public UserDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public User create(
            String username,
            String password,
            String firstname,
            String surname,
            String email,
            Date birthDate,
            String imagePath,
            String role) {

        // Ottengo il timestamp corrente
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        PreparedStatement ps;
        String sql;
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstname(firstname);
        user.setSurname(surname);
        user.setEmail(email);
        user.setBirthDate(birthDate);
        user.setRegistrationTimestamp(currentTimestamp);
        user.setImagePath(imagePath);
        user.setRole(role);
        user.setDeleted(false);

        try{
            sql
                    = "INSERT INTO USER "
                    + "(username,"
                    + "password,"
                    + "firstname,"
                    + "surname,"
                    + "email,"
                    + "birthDate,"
                    + "registrationTimestamp,"
                    + "imagePath,"
                    + "role,"
                    + "deleted) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, user.getUsername());
            ps.setString(i++, user.getPassword());
            ps.setString(i++, user.getFirstname());
            ps.setString(i++, user.getSurname());
            ps.setString(i++, user.getEmail());
            ps.setDate(i++, user.getBirthDate());
            ps.setTimestamp(i++, user.getRegistrationTimestamp());
            ps.setString(i++, user.getImagePath());
            ps.setString(i++, user.getRole());
            ps.setString(i++, "N");

            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    public void update(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public User findLoggedUser() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public User findByUserID(Long userID) {

        PreparedStatement ps;
        User user = null;

        try {

            String sql
                    = " SELECT * "
                    + "   FROM USER "
                    + " WHERE "
                    + "   userID = ?";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, userID);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                user = read(resultSet);
            }
            resultSet.close();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;

    }

    @Override
    public User findByUsername(String username) {

        PreparedStatement ps;
        User user = null;

        try {

            String sql
                    = " SELECT * "
                    + "   FROM USER "
                    + " WHERE "
                    + "   username = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                user = read(resultSet);
            }
            resultSet.close();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;

    }

    User read(ResultSet rs) {

        User user = new User();
        try {
            user.setUserID(rs.getLong("userID"));
        } catch (SQLException sqle) {

        }

        try {
            user.setUsername(rs.getString("username"));
        } catch (SQLException sqle) {

        }

        try {
            user.setPassword(rs.getString("password"));
        } catch (SQLException sqle) {

        }

        try {
            user.setFirstname(rs.getString("firstname"));
        } catch (SQLException sqle) {

        }

        try {
            user.setSurname(rs.getString("surname"));
        } catch (SQLException sqle) {

        }

        try {
            user.setEmail(rs.getString("email"));
        } catch (SQLException sqle) {

        }

        try {
            user.setBirthDate(rs.getDate("birthDate"));
        } catch (SQLException sqle) {

        }

        try {
            user.setRegistrationTimestamp(rs.getTimestamp("registrationTimestamp"));
        } catch (SQLException sqle) {

        }

        try {
            user.setImagePath(rs.getString("imagePath"));
        } catch (SQLException sqle) {

        }

        try {
            user.setRole(rs.getString("role"));
        } catch (SQLException sqle) {

        }

        try {
            user.setDeleted(rs.getString("deleted").equals("Y"));
        } catch (SQLException sqle) {

        }

        return user;
    }

}
