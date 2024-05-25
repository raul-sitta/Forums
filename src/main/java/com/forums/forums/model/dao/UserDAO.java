package com.forums.forums.model.dao;

import com.forums.forums.model.mo.User;

import java.sql.Date;

public interface UserDAO {

    public User create(
            String username,
            String password,
            String firstname,
            String surname,
            String email,
            Date birthDate,
            String imagePath,
            String role);

    public void update(User user);

    public void delete(User user);

    public User findLoggedUser();

    public User findByUserID(Long userID);

    public User findByUsername(String username);

}
