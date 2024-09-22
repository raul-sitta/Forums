package com.forums.forums.model.dao;

import com.forums.forums.model.mo.*;

import java.sql.Timestamp;
import java.util.List;

public interface MediaDAO {
    public Media create(
            String path,
            Timestamp creationTimestamp,
            User uploader,
            Post post
    );
    public void update(Media media);
    public void delete(Media media);
    public Media findByMediaID(Long mediaID);
    public List<Media> getAll();
}
