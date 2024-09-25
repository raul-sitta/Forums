package com.forums.forums.model.mo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class FAQ {

    private Long faqID;
    private String question;
    private String answer;
    private Timestamp creationTimestamp;
    private Boolean deleted;

    /* N:1 */
    private User author;

    public Long getFaqID() {
        return faqID;
    }

    public void setFaqID(Long faqID) {
        this.faqID = faqID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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
}
