package com.task.management.system.model.entity;

public interface CommentProjection {
    Long getId();
    String getContent();
    String getAuthorId();
    String getAuthorEmail();
    Long getTaskId();
}
