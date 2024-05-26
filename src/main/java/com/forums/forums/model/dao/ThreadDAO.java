package com.forums.forums.model.dao;

import com.forums.forums.model.mo.Thread;
import com.forums.forums.model.mo.User;
import com.forums.forums.model.mo.Category;

public interface ThreadDAO {
    public Thread create(
            String title,
            User author,
            Category category,
            Boolean anonymous
    );
    public void update(Thread thread);
    public void delete(Thread thread);
}
