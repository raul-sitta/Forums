package com.forums.forums.model.mo;

import java.sql.Timestamp;

public class Media {

    private Long mediaID;
    private String path;
    private Timestamp creationTimestamp;
    private User uploader;
    private String linkedResourceType;
    private Long linkedResourceID;
    private Boolean deleted;

    public Long getMediaID() {
        return mediaID;
    }

    public void setMediaID(Long mediaID) {
        this.mediaID = mediaID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public String getLinkedResourceType() {
        return linkedResourceType;
    }

    public void setLinkedResourceType(String linkedResourceType) {
        this.linkedResourceType = linkedResourceType;
    }

    public Long getLinkedResourceID() {
        return linkedResourceID;
    }

    public void setLinkedResourceID(Long linkedResourceID) {
        this.linkedResourceID = linkedResourceID;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
