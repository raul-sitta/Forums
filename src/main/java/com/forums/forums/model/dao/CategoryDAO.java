package com.forums.forums.model.dao;

import com.forums.forums.model.dao.exception.DuplicatedObjectException;
import com.forums.forums.model.mo.Category;

public interface CategoryDAO {
    public Category create(
            String name
    ) throws DuplicatedObjectException;
    public void update(Category category) throws DuplicatedObjectException;
    public void delete(Category category);
}
