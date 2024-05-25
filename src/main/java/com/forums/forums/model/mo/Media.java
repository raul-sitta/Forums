package com.forums.forums.model.mo;

public class Media {

    private Long mediaID;
    private String path;
    private User uploader;
    private Post post;
    private Report report;
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

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}
