package com.task.management.system.model.dto;

import com.task.management.system.enums.Priority;
import com.task.management.system.enums.Status;
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