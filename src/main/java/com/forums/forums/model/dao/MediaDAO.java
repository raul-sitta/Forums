package com.forums.forums.model.dao;

import com.forums.forums.model.mo.*;

import java.util.List;

public interface MediaDAO {
    public Media create(
            String path,
            User uploader,
            String linkedResourceType,
            Long linkedResourceID
    );
    public void update(Media media);
    public void delete(Media media);
    public List<Media> getAll();
}
