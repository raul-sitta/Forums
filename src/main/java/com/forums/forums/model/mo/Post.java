package com.forums.forums.model.mo;

import java.sql.Timestamp;

public class Post {

    private Long postID;
    private String content;
    private Timestamp creationTimestamp;
    private Boolean deleted;

    /* N:1 */
    private User author;
    private Topic topic;
    private Post parentPost;

    /* 1:N */
    private Media[] medias;
    private Post[] childrenPosts;

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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Post getParentPost() {
        return parentPost;
    }

    public void setParentPost(Post parentPost) {
        this.parentPost = parentPost;
    }

    public Media[] getMedias() {
        return medias;
    }

    public void setMedias(Media[] medias) {
        this.medias = medias;
    }

    public Post[] getChildrenPosts() {
        return childrenPosts;
    }

    public void setChildrenPosts(Post[] childrenPosts) {
        this.childrenPosts = childrenPosts;
    }
}
