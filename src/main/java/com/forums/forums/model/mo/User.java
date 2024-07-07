package com.forums.forums.model.mo;

import java.sql.Date;
import java.sql.Timestamp;

public class User {

    private Long userID;
    private String username;
    private String password;
    private String firstname;
    private String surname;
    private String email;
    private Date birthDate;
    private Timestamp registrationTimestamp;
    private String rank;
    private Boolean deleted;

    /* 1:N */
    private Topic[] topics;
    private Post[] posts;
    private Media[] medias;
    private Report[] reportsCreated;
    private Report[] reportsReceived;

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Timestamp getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(Timestamp registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Topic[] getTopics() {
        return topics;
    }

    public void setTopics(Topic[] topics) {
        this.topics = topics;
    }

    public Post[] getPosts() {
        return posts;
    }

    public void setPosts(Post[] posts) {
        this.posts = posts;
    }

    public Media[] getMedias() {
        return medias;
    }

    public void setMedias(Media[] medias) {
        this.medias = medias;
    }

    public Report[] getReportsCreated() {
        return reportsCreated;
    }

    public void setReportsCreated(Report[] reportsCreated) {
        this.reportsCreated = reportsCreated;
    }

    public Report[] getReportsReceived() {
        return reportsReceived;
    }

    public void setReportsReceived(Report[] reportsReceived) {
        this.reportsReceived = reportsReceived;
    }
}
