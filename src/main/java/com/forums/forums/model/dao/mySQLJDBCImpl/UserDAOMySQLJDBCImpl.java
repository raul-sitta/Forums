package com.forums.forums.model.dao.mySQLJDBCImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.forums.forums.model.mo.User;
import com.forums.forums.model.dao.UserDAO;

import com.forums.forums.model.dao.exception.DuplicatedObjectException;

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
            String role) throws DuplicatedObjectException {

        // Ottengo il timestamp corrente
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        PreparedStatement ps;
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
            String sql
                    = " SELECT userID "
                    + " FROM USER "
                    + " WHERE "
                    + " deleted = 'N' AND "
                    + " username = ? ";

            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());

            ResultSet resultSet = ps.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new DuplicatedObjectException("UserDAOJDBCImpl.create: Tentativo di inserimento di un utente già esistente.");
            }

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
    public void update(User user) throws DuplicatedObjectException {
        PreparedStatement ps;

        try{
            String sql
                    = " SELECT userID "
                    + " FROM USER "
                    + " WHERE "
                    + " deleted = 'N' AND "
                    + " username = ? ";

            ps = conn.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, user.getUsername());

            ResultSet resultSet = ps.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new DuplicatedObjectException("UserDAOJDBCImpl.update: Tentativo di aggiornamento di un utente già esistente.");
            }

            sql
                    = "UPDATE USER "
                    + "SET "
                    + "username = ?, "
                    + "password = ?, "
                    + "firstname = ?, ,"
                    + "surname = ?, "
                    + "email = ?, "
                    + "birthDate = ?, "
                    + "imagePath = ?, "
                    + "role = ? "
                    + "WHERE userID = ?";
            ps = conn.prepareStatement(sql);

            i = 1;
            ps.setString(i++, user.getUsername());
            ps.setString(i++, user.getPassword());
            ps.setString(i++, user.getFirstname());
            ps.setString(i++, user.getSurname());
            ps.setString(i++, user.getEmail());
            ps.setDate(i++, user.getBirthDate());
            ps.setString(i++, user.getImagePath());
            ps.setString(i++, user.getRole());

            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(User user) {
        PreparedStatement ps;
        String sql;

        try {
            sql
                    = "UPDATE USER SET "
                    + "deleted = ? "
                    + "WHERE userID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Y");
            ps.setLong(2, user.getUserID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public List<User> getAll() {
        PreparedStatement ps;
        List<User> users = new ArrayList<>();

        try {
            String sql = "SELECT * FROM USER";

            ps = conn.prepareStatement(sql);

            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()){
                User user = read(resultSet);
                users.add(user);
            }
            resultSet.close();
            ps.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return users;
    }

    User read(ResultSet rs) {

        User user = new User();
        try {

            user.setUserID(rs.getLong("userID"));

            user.setUsername(rs.getString("username"));

            user.setPassword(rs.getString("password"));

            user.setFirstname(rs.getString("firstname"));

            user.setSurname(rs.getString("surname"));

            user.setEmail(rs.getString("email"));

            user.setBirthDate(rs.getDate("birthDate"));

            user.setRegistrationTimestamp(rs.getTimestamp("registrationTimestamp"));

            user.setImagePath(rs.getString("imagePath"));

            user.setRole(rs.getString("role"));

            user.setDeleted(rs.getString("deleted").equals("Y"));

        } catch (SQLException sqle) {

            throw new RuntimeException("Error: read rs - User", sqle);

        }

        return user;
    }

}
