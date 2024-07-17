package com.forums.forums.model.dao;

import com.forums.forums.model.dao.exception.DuplicatedObjectException;
import com.forums.forums.model.mo.*;

import java.sql.*;
import java.util.List;

public interface UserDAO {

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
            Boolean hasProfilePic) throws DuplicatedObjectException;

    public void update(User user) throws DuplicatedObjectException;

    public void delete(User user);

    public User findLoggedUser();

    public User findByUserID(Long userID);

    public User findByUsername(String username);

    public List<User> findByParameters(String username,
                                       Date registratedBefore,
                                       Date registratedAfter,
                                       String role,
                                       Boolean isDeleted,
                                       User exceptUser);

    public List<User> getAll();

}
