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

    @Test
    void createUserTask_ValidInput_CreatesTask() {
        TaskDto taskDto = new TaskDto();
        doNothing().when(taskService).createTask(taskDto);

        ResponseEntity<String> response = userController.createUserTask(taskDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task created", response.getBody());
        verify(taskService).createTask(taskDto);
    }

    @Test
    void updateUserTask_ValidInput_UpdatesTask() {
        TaskDto taskDto = new TaskDto();
        doNothing().when(taskService).editTask(taskDto);

        ResponseEntity<String> response = userController.updateUserTask(taskDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task updated", response.getBody());
        verify(taskService).editTask(taskDto);
    }

    @Test
    void deleteUserTask_DeletesTask() {
        Long taskId = 1L;
        doNothing().when(taskService).deleteTaskById(taskId);

        ResponseEntity<String> response = userController.deleteUserTask(taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task deleted", response.getBody());
        verify(taskService).deleteTaskById(taskId);
    }
}