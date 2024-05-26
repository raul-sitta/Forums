package com.forums.forums.model.dao;

import com.forums.forums.model.dao.exception.DuplicatedObjectException;
import com.forums.forums.model.mo.*;

import java.util.List;

public interface CategoryDAO {
    public Category create(
            String name
    ) throws DuplicatedObjectException;
    public void update(Category category) throws DuplicatedObjectException;
    public void delete(Category category);
    public Category findByCategoryID(Long categoryID);
    public Category findByName(String name);
    public List<Category> getAll();
}
