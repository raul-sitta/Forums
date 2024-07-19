package com.forums.forums.model.dao.mySQLJDBCImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import com.forums.forums.model.mo.*;
import com.forums.forums.model.dao.UserDAO;

import com.forums.forums.model.dao.exception.DuplicatedObjectException;
import com.forums.forums.services.filesystemservice.FileSystemService;

public class UserDAOMySQLJDBCImpl implements UserDAO {

    private final String COUNTER_ID = "userID";
    Connection conn;

    public UserDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public User create(
            Long userID,
            String username,
            String password,
            String firstname,
            String surname,
            String email,
            Date birthDate,
            Timestamp registrationTimestamp,
            String role,
            Boolean hasProfilePic) throws DuplicatedObjectException {

        PreparedStatement ps;
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstname(firstname);
        user.setSurname(surname);
        user.setEmail(email);
        user.setBirthDate(birthDate);
        user.setRegistrationTimestamp(registrationTimestamp);
        user.setRole(role);
        user.setDeleted(false);

        try{
            // Controllo se esiste già un utente con lo stesso username o email
            String sql
                    = " SELECT * "
                    + " FROM USER "
                    + " WHERE "
                    + " (deleted = 'N') AND "
                    + " (username = ? OR "
                    + " email = ?) ";

            ps = conn.prepareStatement(sql);
            int i=1;
            ps.setString(i++, user.getUsername());
            ps.setString(i++, user.getEmail());

            ResultSet resultSet = ps.executeQuery();

            boolean exist = false;
            User duplicatedUser = null;
            String duplicatedAttribute = null;
            if (resultSet.next()) {
                exist = true;
                duplicatedUser = read(resultSet);
                if (user.getEmail().equals(duplicatedUser.getEmail())) duplicatedAttribute = "email";
                if (user.getUsername().equals(duplicatedUser.getUsername())) duplicatedAttribute = "username";
            }
            resultSet.close();

            if (exist) {
                throw new DuplicatedObjectException("UserDAOJDBCImpl.create: Tentativo di inserimento di un utente già esistente.", duplicatedAttribute);
            }

            sql = "UPDATE COUNTER SET counterValue=counterValue+1 where counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            sql = "SELECT counterValue FROM COUNTER WHERE counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            resultSet = ps.executeQuery();
            resultSet.next();

            user.setUserID(resultSet.getLong("counterValue"));

            resultSet.close();

            sql
                    = "INSERT INTO USER "
                    + "(userID,"
                    + "username,"
                    + "password,"
                    + "firstname,"
                    + "surname,"
                    + "email,"
                    + "birthDate,"
                    + "registrationTimestamp,"
                    + "role,"
                    + "profilePicPath,"
                    + "deleted) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            i = 1;
            ps.setLong(i++, user.getUserID());
            ps.setString(i++, user.getUsername());
            ps.setString(i++, user.getPassword());
            ps.setString(i++, user.getFirstname());
            ps.setString(i++, user.getSurname());
            ps.setString(i++, user.getEmail());
            ps.setDate(i++, user.getBirthDate());
            ps.setTimestamp(i++, user.getRegistrationTimestamp());
            ps.setString(i++, user.getRole());
            ps.setString(i++, (hasProfilePic) ?
                    FileSystemService.getUserRelativeProfilePicPath(user.getUserID()) :
                    FileSystemService.DEFAULT_PROFILE_PIC_PATH);
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
                    = " SELECT * "
                    + " FROM USER "
                    + " WHERE "
                    + " (deleted = 'N') AND "
                    + " (userID <> ?) AND "
                    + " (username = ? OR "
                    + " email = ?)";

            ps = conn.prepareStatement(sql);
            int i=1;
            ps.setLong(i++, user.getUserID());
            ps.setString(i++, user.getUsername());
            ps.setString(i++, user.getEmail());

            ResultSet resultSet = ps.executeQuery();

            boolean exist = false;
            User duplicatedUser = null;
            String duplicatedAttribute = null;
            if (resultSet.next()) {
                exist = true;
                duplicatedUser = read(resultSet);
                if (user.getEmail().equals(duplicatedUser.getEmail())) duplicatedAttribute = "email";
                if (user.getUsername().equals(duplicatedUser.getUsername())) duplicatedAttribute = "username";
            }
            resultSet.close();

            if (exist) {
                throw new DuplicatedObjectException("UserDAOJDBCImpl.create: Tentativo di inserimento di un utente già esistente.", duplicatedAttribute);
            }

            sql
                    = "UPDATE USER "
                    + "SET "
                    + "username = ?, "
                    + "password = ?, "
                    + "firstname = ?, "
                    + "surname = ?, "
                    + "email = ?, "
                    + "birthDate = ?, "
                    + "role = ?, "
                    + "profilePicPath = ? "
                    + "WHERE userID = ?";
            ps = conn.prepareStatement(sql);

            i = 1;
            ps.setString(i++, user.getUsername());
            ps.setString(i++, user.getPassword());
            ps.setString(i++, user.getFirstname());
            ps.setString(i++, user.getSurname());
            ps.setString(i++, user.getEmail());
            ps.setDate(i++, user.getBirthDate());
            ps.setString(i++, user.getRole());
            ps.setString(i++, user.getProfilePicPath());
            ps.setLong(i++, user.getUserID());
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
            sql =     "UPDATE USER SET "
                    + "deleted = ?, "
                    + "profilePicPath = ? "
                    + "WHERE userID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Y");
            ps.setString(2, FileSystemService.DELETED_PROFILE_PIC_PATH);
            ps.setLong(3, user.getUserID());

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
                    + " FROM USER "
                    + " WHERE username = ? "
                    + " AND deleted = ? ";

            ps = conn.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, username);
            ps.setString(i++, "N");

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

    @Override
    public List<User> findByParameters(String username,
                                       Date registratedBefore,
                                       Date registratedAfter,
                                       String role,
                                       Boolean isDeleted,
                                       User exceptUser) {
        PreparedStatement ps;
        List<User> users = new ArrayList<>();

        Timestamp registratedBeforeTimestamp = null;
        if (registratedBefore != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(registratedBefore);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            registratedBeforeTimestamp = new Timestamp(cal.getTimeInMillis());
        }

        Timestamp registratedAfterTimestamp = null;
        if (registratedBefore != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(registratedAfter);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            registratedAfterTimestamp = new Timestamp(cal.getTimeInMillis());
        }


        try {
            String sql = "SELECT * FROM USERS WHERE 1=1 "; // Base SQL query

            // Costruzione dinamica della query SQL
            if (username != null && !username.trim().isEmpty()) {
                sql += "AND username LIKE ? ";
            }
            if (registratedBefore != null) {
                sql += "AND registrationTimestamp < ? ";
            }
            if (registratedAfter != null) {
                sql += "AND registrationTimestamp > ? ";
            }
            if (role != null && !role.trim().isEmpty()) {
                sql += "AND role = ? ";
            }
            if (isDeleted != null) {
                sql += "AND deleted = ? ";
            }
            if (exceptUser != null) {
                sql += "AND userID <> ? ";
            }

            ps = conn.prepareStatement(sql);

            int i = 1;
            if (username != null && !username.trim().isEmpty()) {
                ps.setString(i++, "%" + username + "%");
            }
            if (registratedBefore != null) {
                ps.setTimestamp(i++, registratedBeforeTimestamp);
            }
            if (registratedAfter != null) {
                ps.setTimestamp(i++, registratedAfterTimestamp);
            }
            if (role != null && !role.trim().isEmpty()) {
                ps.setString(i++, role);
            }
            if (isDeleted != null) {
                ps.setString(i++, isDeleted ? "Y" : "N");
            }
            if (exceptUser != null) {
                ps.setLong(i++, exceptUser.getUserID());
            }

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
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

    @Override
    public User findByPost(Post post) {
        PreparedStatement ps;
        User user = null;

        try {

            String sql = "SELECT U.* " +
                         "FROM USER AS U " +
                         "JOIN POST AS P ON U.userID = P.authorID " +
                         "WHERE P.postID = ? ";

            ps = conn.prepareStatement(sql);
            int i = 1;
            ps.setLong(i++, post.getPostID());

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
            user.setRole(rs.getString("role"));
        } catch (SQLException sqle) {
        }

        try {
            user.setProfilePicPath(rs.getString("profilePicPath"));
        } catch (SQLException sqle) {
        }

        try {
            user.setDeleted(rs.getString("deleted").equals("Y"));
        } catch (SQLException sqle) {
        }

        return user;
    }
}
