package com.forums.forums.model.mo;

import java.sql.Timestamp;

public class Post {

    private Long postID;
    private String content;
    private Timestamp creationTimestamp;
    private User author;
    private Thread thread;
    private Boolean deleted;

    /* 1:N */
    private Media[] medias;

    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Media[] getMedias() {
        return medias;
    }

    public void setMedias(Media[] medias) {
        this.medias = medias;
    }

}
