package com.forums.forums.model.dao.CookieImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.forums.forums.model.dao.UserDAO;
import com.forums.forums.model.mo.User;

import java.sql.*;
import java.util.List;


public class UserDAOCookieImpl implements UserDAO {

    HttpServletRequest request;
    HttpServletResponse response;

    public UserDAOCookieImpl(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
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
            String role) {

        User loggedUser = new User();
        loggedUser.setUserID(userID);
        loggedUser.setUsername(username);
        loggedUser.setFirstname(firstname);
        loggedUser.setSurname(surname);
        loggedUser.setRole(role);

        Cookie cookie;
        cookie = new Cookie("loggedUser", encode(loggedUser));
        cookie.setPath("/");
        response.addCookie(cookie);

        return loggedUser;

    }

    @Override
    public void update(User loggedUser) {

        Cookie cookie;
        cookie = new Cookie("loggedUser", encode(loggedUser));
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    @Override
    public void delete(User loggedUser) {

        Cookie cookie;
        cookie = new Cookie("loggedUser", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    @Override
    public User findLoggedUser() {

        Cookie[] cookies = request.getCookies();
        User loggedUser = null;

        if (cookies != null) {
            for (int i = 0; i < cookies.length && loggedUser == null; i++) {
                if (cookies[i].getName().equals("loggedUser")) {
                    loggedUser = decode(cookies[i].getValue());
                }
            }
        }

        return loggedUser;

    }

    @Override
    public User findByUserID(Long userID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public User findByUsername(String username) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<User> getAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<User> findByParameters(String username,
                                       Date registratedBefore,
                                       Date registratedAfter,
                                       String role,
                                       Boolean isDeleted,
                                       User exceptUser) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String encode(User loggedUser) {

        String encodedLoggedUser;
        encodedLoggedUser = loggedUser.getUserID() + "#" + loggedUser.getUsername() + "#" + loggedUser.getFirstname() + "#" + loggedUser.getSurname() + "#" + loggedUser.getRole();
        encodedLoggedUser = encodedLoggedUser.replace(" ", "_");
        return encodedLoggedUser;

    }

    private User decode(String encodedLoggedUser) {

        User loggedUser = new User();

        String[] values = encodedLoggedUser.split("#");

        loggedUser.setUserID(Long.parseLong(values[0].replace("_", " ")));
        loggedUser.setUsername(values[1].replace("_", " "));
        loggedUser.setFirstname(values[2].replace("_", " "));
        loggedUser.setSurname(values[3].replace("_", " "));
        loggedUser.setRole(values[4].replace("_", " "));

        return loggedUser;

    }

}

