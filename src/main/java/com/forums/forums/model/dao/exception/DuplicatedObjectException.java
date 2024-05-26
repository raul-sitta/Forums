package com.forums.forums.model.dao.exception;

public class DuplicatedObjectException extends Exception {

    public DuplicatedObjectException() {
    }

    public DuplicatedObjectException(String msg) {
        super(msg);
    }
}