package com.forums.forums.model.dao;

import com.forums.forums.model.mo.Media;
import com.forums.forums.model.mo.User;
import com.forums.forums.model.mo.Post;
import com.forums.forums.model.mo.Report;

public interface MediaDAO {
    public Media create(
            String path,
            User uploader,
            String linkedResourceType,
            Long linkedResourceID
    );
    public void update(Media media);
    public void delete(Media media);
}
