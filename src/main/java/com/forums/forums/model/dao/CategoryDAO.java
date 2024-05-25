package com.forums.forums.model.dao;

import com.forums.forums.model.mo.Category;

public interface CategoryDAO {
    public Category create(
            String name
    );
    public void update(Category category);
    public void delete(Category category);
}
