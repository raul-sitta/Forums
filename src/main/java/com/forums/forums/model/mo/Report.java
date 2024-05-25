package com.forums.forums.model.mo;

import java.sql.Timestamp;

public class Report {

    private Long reportID;
    private String content;
    private Timestamp creationTimestamp;
    private User author;
    private Post reportedPost;
    private Thread reportedThread;
    private User reportedUser;

    public Long getReportID() {
        return reportID;
    }

    public void setReportID(Long reportID) {
        this.reportID = reportID;
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

    public Post getReportedPost() {
        return reportedPost;
    }

    public void setReportedPost(Post reportedPost) {
        this.reportedPost = reportedPost;
    }

    public Thread getReportedThread() {
        return reportedThread;
    }

    public void setReportedThread(Thread reportedThread) {
        this.reportedThread = reportedThread;
    }

    public User getReportedUser() {
        return reportedUser;
    }

    public void setReportedUser(User reportedUser) {
        this.reportedUser = reportedUser;
    }

}
