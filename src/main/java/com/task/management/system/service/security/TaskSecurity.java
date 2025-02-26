package com.task.management.system.service.security;

import com.task.management.system.repository.TaskRepository;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
public class TaskSecurity {

    private final TaskRepository taskRepository;

    public TaskSecurity(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public boolean isAssignee(String email) {
        if(email != null) {
            return taskRepository.existsByAssignee_Email(email);
        }
        return false;
    }

    public boolean isAuthor(String email) {
        if(email != null) {
            return taskRepository.existsByAuthor_Email(email);
        }
        return false;
    }
}
