package com.task.management.system.controller;

import com.task.management.system.exception.EntityNotFoundException;
import com.task.management.system.model.dto.TaskDto;
import com.task.management.system.service.CommentService;
import com.task.management.system.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private UserController userController;

    @Test
    void getUserTask_TaskExists_ReturnsTask() {
        Long taskId = 1L;
        TaskDto taskDto = new TaskDto();
        when(taskService.getTaskById(taskId)).thenReturn(taskDto);

        ResponseEntity<TaskDto> response = userController.getUserTask(taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskDto, response.getBody());
    }

    @Test
    void getUserTask_TaskDoesNotExist_ThrowsException() {
        Long taskId = 1L;
        when(taskService.getTaskById(taskId)).thenThrow(new EntityNotFoundException("Task not found"));

        assertThrows(EntityNotFoundException.class, () -> userController.getUserTask(taskId));
    }
}