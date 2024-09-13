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
                    + " (userDeleted = 'N') AND "
                    + " (userUsername = ? OR "
                    + " userEmail = ?) ";

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
                    + "userUsername,"
                    + "userPassword,"
                    + "userFirstname,"
                    + "userSurname,"
                    + "userEmail,"
                    + "userBirthDate,"
                    + "userRegistrationTimestamp,"
                    + "userRole,"
                    + "userProfilePicPath,"
                    + "userDeleted) "
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
                    + " (userDeleted = 'N') AND "
                    + " (userID <> ?) AND "
                    + " (userUsername = ? OR "
                    + " userEmail = ?)";

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
                    + "userUsername = ?, "
                    + "userPassword = ?, "
                    + "userFirstname = ?, "
                    + "userSurname = ?, "
                    + "userEmail = ?, "
                    + "userBirthDate = ?, "
                    + "userRole = ?, "
                    + "userProfilePicPath = ? "
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

        user.setDeleted(true);
        user.setProfilePicPath(FileSystemService.DELETED_PROFILE_PIC_PATH);

        try {
            sql =     "UPDATE USER SET "
                    + "userDeleted = ?, "
                    + "userProfilePicPath = ? "
                    + "WHERE userID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Y");
            ps.setString(2, user.getProfilePicPath());
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
    public User findByUserIDWithStats(Long userID, List<Long> userStats) {

        PreparedStatement ps;
        User user = null;

        try {

            String sql =    "SELECT " +
                            "    U.userID, " +
                            "    U.userUsername, " +
                            "    U.userPassword, " +
                            "    U.userFirstname, " +
                            "    U.userSurname, " +
                            "    U.userEmail, " +
                            "    U.userBirthDate, " +
                            "    U.userRegistrationTimestamp, " +
                            "    U.userRole, " +
                            "    U.userProfilePicPath, " +
                            "    U.userDeleted, " +
                            "    COUNT(DISTINCT T.topicID) AS topicCount, " +
                            "    COUNT(DISTINCT P.postID) AS postCount " +
                            "FROM " +
                            "    USER AS U " +
                            "LEFT JOIN " +
                            "    TOPIC AS T ON T.topicAuthorID = U.userID AND T.topicDeleted = 'N' " +
                            "LEFT JOIN " +
                            "    POST AS P ON P.postAuthorID = U.userID AND P.postDeleted = 'N' " +
                            "WHERE " +
                            "    U.userID = ? " +
                            "GROUP BY " +
                            "    U.userID, " +
                            "    U.userUsername, " +
                            "    U.userPassword, " +
                            "    U.userFirstname, " +
                            "    U.userSurname, " +
                            "    U.userEmail, " +
                            "    U.userBirthDate, " +
                            "    U.userRegistrationTimestamp, " +
                            "    U.userRole, " +
                            "    U.userProfilePicPath, " +
                            "    U.userDeleted ";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, userID);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                user = read(resultSet);

                try {
                    userStats.add(resultSet.getLong("topicCount"));
                } catch (SQLException sqle) {
                }

                try {
                    userStats.add(resultSet.getLong("postCount"));
                } catch (SQLException sqle) {
                }

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
                    + " WHERE userUsername = ? "
                    + " AND userDeleted = ? ";

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
            String sql = "SELECT * FROM USERS WHERE 1=1 ";

            // Costruzione dinamica della query SQL
            if (username != null && !username.trim().isEmpty()) {
                sql += "AND userUsername LIKE ? ";
            }
            if (registratedBefore != null) {
                sql += "AND userRegistrationTimestamp < ? ";
            }
            if (registratedAfter != null) {
                sql += "AND userRegistrationTimestamp > ? ";
            }
            if (role != null && !role.trim().isEmpty()) {
                sql += "AND userRole = ? ";
            }
            if (isDeleted != null) {
                sql += "AND userDeleted = ? ";
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
                         "JOIN POST AS P ON U.userID = P.postAuthorID " +
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
            user.setUsername(rs.getString("userUsername"));
        } catch (SQLException sqle) {
        }

        try {
            user.setPassword(rs.getString("userPassword"));
        } catch (SQLException sqle) {
        }

        try {
            user.setFirstname(rs.getString("userFirstname"));
        } catch (SQLException sqle) {
        }

        try {
            user.setSurname(rs.getString("userSurname"));
        } catch (SQLException sqle) {
        }

        try {
            user.setEmail(rs.getString("userEmail"));
        } catch (SQLException sqle) {
        }

        try {
            user.setBirthDate(rs.getDate("userBirthDate"));
        } catch (SQLException sqle) {
        }

        try {
            user.setRegistrationTimestamp(rs.getTimestamp("userRegistrationTimestamp"));
        } catch (SQLException sqle) {
        }

        try {
            user.setRole(rs.getString("userRole"));
        } catch (SQLException sqle) {
        }

        try {
            user.setProfilePicPath(rs.getString("userProfilePicPath"));
        } catch (SQLException sqle) {
        }

        try {
            user.setDeleted(rs.getString("userDeleted").equals("Y"));
        } catch (SQLException sqle) {
        }

        return user;
    }
}
