package com.forums.forums.model.dao;

import com.forums.forums.model.dao.exception.DuplicatedObjectException;
import com.forums.forums.model.mo.*;

import java.sql.Date;
import java.util.List;

public interface UserDAO {

    public User create(
            String username,
            String password,
            String firstname,
            String surname,
            String email,
            Date birthDate,
            String imagePath,
            String role) throws DuplicatedObjectException;

    public void update(User user) throws DuplicatedObjectException;

    public void delete(User user);

    public User findLoggedUser();

    public User findByUserID(Long userID);

    public User findByUsername(String username);

    public List<User> getAll();

}
