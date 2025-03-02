package com.task.management.system.service.security;

import com.task.management.system.repository.TaskRepository;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
public class TaskSecurity {

    private final TaskRepository taskRepository;

    public TaskSecurity(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public boolean isAssignee(String email, Long taskId) {
        if(email != null && taskId != null) {
            return taskRepository.existsByIdAndAssignee_Email(taskId, email);
        }
        return false;
    }

    public boolean isAuthor(String email, Long taskId) {
        if(email != null && taskId != null) {
            return taskRepository.existsByIdAndAuthor_Email(taskId, email);
        }
        return false;
    }
}
