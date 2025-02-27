package com.task.management.system.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskFilter {
    private String status;
    private String priority;
    private String authorEmail;
    private String assigneeEmail;
}