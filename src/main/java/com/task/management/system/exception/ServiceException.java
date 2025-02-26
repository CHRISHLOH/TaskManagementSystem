package com.task.management.system.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final String messageCode;
    private final Object[] args;

    public ServiceException(String messageCode) {
        super(messageCode);
        this.messageCode = messageCode;
        this.args = new Object[0];
    }

    public ServiceException(String messageCode, Object... args) {
        super(messageCode);
        this.messageCode = messageCode;
        this.args = args;
    }

    public ServiceException(String messageCode, Throwable cause) {
        super(messageCode, cause);
        this.messageCode = messageCode;
        this.args = new Object[0];
    }

    public ServiceException(String messageCode, Throwable cause, Object... args) {
        super(messageCode, cause);
        this.messageCode = messageCode;
        this.args = args;
    }
}

