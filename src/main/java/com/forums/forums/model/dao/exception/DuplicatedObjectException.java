package com.forums.forums.model.dao.exception;

public class DuplicatedObjectException extends Exception {

    private String duplicatedAttribute;

    public DuplicatedObjectException() {
    }

    public DuplicatedObjectException(String msg) {
        super(msg);
    }

    public DuplicatedObjectException(String msg, String duplicatedAttribute) {
        super(msg);
        this.duplicatedAttribute=duplicatedAttribute;
    }

    public String getDuplicatedAttribute() {
        return duplicatedAttribute;
    }

    public void setDuplicatedAttribute(String duplicatedAttribute) {
        this.duplicatedAttribute = duplicatedAttribute;
    }

}