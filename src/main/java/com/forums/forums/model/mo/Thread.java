package com.forums.forums.model.mo;

import java.sql.Timestamp;

public class Thread {

    private Long threadID;
    private String title;
    private Timestamp creationTimestamp;
    private User author;
    private Category category;
    private Boolean anonymous;
    private Boolean deleted;

    /* 1:N */
    private Post[] posts;

    public Long getThreadID() {
        return threadID;
    }

    public void setThreadID(Long threadID) {
        this.threadID = threadID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Post[] getPosts() {
        return posts;
    }

    public void setPosts(Post[] posts) {
        this.posts = posts;
    }
}
