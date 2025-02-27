package com.task.management.system.controller;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import com.task.management.system.exception.EntityNotFoundException;
import com.task.management.system.model.dto.TaskDto;
import com.task.management.system.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void getTaskById_TaskExists_ReturnsTask() {
        Long taskId = 1L;
        TaskDto taskDto = new TaskDto();
        when(taskService.getTaskById(taskId)).thenReturn(taskDto);

        ResponseEntity<TaskDto> result = adminController.getTaskById(taskId);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(taskDto, result.getBody());
    }

    @Test
    void getTaskById_TaskDoesNotExist_ThrowsException() {
        Long taskId = 1L;
        when(taskService.getTaskById(taskId)).thenThrow(new EntityNotFoundException("Задача не найдена"));

        assertThrows(EntityNotFoundException.class, () -> adminController.getTaskById(taskId));
    }

    @Test
    void updateTask_RequestIsValid_ReturnsOk() {
        TaskDto taskDto = new TaskDto();

        doNothing().when(taskService).editTask(taskDto);

        ResponseEntity<String> result = adminController.updateTask(taskDto);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Задача обновлена", result.getBody());
        verify(taskService).editTask(taskDto);
    }

    @Test
    void addTask_RequestIsValid_ReturnsOk() {
        TaskDto taskDto = new TaskDto();
        doNothing().when(taskService).createTask(taskDto);

        ResponseEntity<String> result = adminController.addTask(taskDto);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Задача создана", result.getBody());
        verify(taskService).createTask(taskDto);
    }

    @Test
    void deleteTask_ReturnsOk() {
        Long taskId = 1L;
        doNothing().when(taskService).deleteTaskById(taskId);

        ResponseEntity<String> result = adminController.deleteTask(taskId);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Задача удалена", result.getBody());
        verify(taskService).deleteTaskById(taskId);
    }

}
