package com.task.management.system.exception;

public class EntityNotFoundException extends ServiceException {

    public EntityNotFoundException(String messageCode) {
        super(messageCode);
    }

    public EntityNotFoundException(String messageCode, Object... args) {
        super(messageCode, args);
    }
}
