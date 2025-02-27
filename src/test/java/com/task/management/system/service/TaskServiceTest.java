package com.task.management.system.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import com.task.management.system.enums.Priority;
import com.task.management.system.enums.Status;
import com.task.management.system.exception.EntityNotFoundException;
import com.task.management.system.mapper.CommentMapper;
import com.task.management.system.mapper.TaskMapper;
import com.task.management.system.model.dto.CommentDto;
import com.task.management.system.model.dto.TaskDto;
import com.task.management.system.model.dto.TaskFilter;
import com.task.management.system.model.dto.UserDto;
import com.task.management.system.model.entity.Comment;
import com.task.management.system.model.entity.CommentProjection;
import com.task.management.system.model.entity.Task;
import com.task.management.system.model.entity.User;
import com.task.management.system.repository.CommentRepository;
import com.task.management.system.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private TaskService taskService;

    private TaskDto sampleTaskDto;
    private Task sampleTask;
    private List<Comment> sampleComments;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых объектов
        User sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("user@example.com");
        sampleUser.setPassword("password");

        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Test Task");
        sampleTask.setDescription("Test Description");
        sampleTask.setStatus(Status.PENDING);
        sampleTask.setPriority(Priority.MEDIUM);
        sampleTask.setAuthor(sampleUser);
        sampleTask.setAssignee(sampleUser);

        sampleTaskDto = new TaskDto();
        sampleTaskDto.setId(1L);
        sampleTaskDto.setTitle("Test Task");
        sampleTaskDto.setDescription("Test Description");
        sampleTaskDto.setStatus("В ожидании");   // строковое представление, которое маппер преобразует в Status
        sampleTaskDto.setPriority("Средняя");
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("user@example.com");
        sampleTaskDto.setAuthor(userDto);
        sampleTaskDto.setAssignee(userDto);

        // Создадим один комментарий для задачи
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");
        comment.setAuthor(sampleUser);
        comment.setTask(sampleTask);
        sampleComments = new ArrayList<>();
        sampleComments.add(comment);
    }

    @Test
    void testCreateTask() {
        // when: маппер преобразует TaskDto в сущность Task
        when(taskMapper.toEntity(sampleTaskDto)).thenReturn(sampleTask);
        // when: репозиторий сохраняет задачу и возвращает её
        when(taskRepository.save(sampleTask)).thenReturn(sampleTask);

        taskService.createTask(sampleTaskDto);

        verify(taskMapper, times(1)).toEntity(sampleTaskDto);
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    void testEditTask_TaskNotFound() {
        when(taskRepository.findTaskByIdWithUsers(sampleTaskDto.getId())).thenReturn(null);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.editTask(sampleTaskDto));
        assertTrue(exception.getMessage().contains("task.not.found"));
    }

    @Test
    void testEditTask_Success() {
        when(taskRepository.findTaskByIdWithUsers(sampleTaskDto.getId())).thenReturn(sampleTask);
        sampleTaskDto.setAssignee(null);

        taskService.editTask(sampleTaskDto);

        verify(taskRepository, times(1)).save(sampleTask);
        assertEquals(sampleTaskDto.getTitle(), sampleTask.getTitle());
        assertEquals(Status.fromDisplayName(sampleTaskDto.getStatus()), sampleTask.getStatus());
        assertEquals(Priority.fromDisplayName(sampleTaskDto.getPriority()), sampleTask.getPriority());
    }

    @Test
    void testGetTaskById_TaskNotFound() {
        when(taskRepository.findTaskByIdWithUsers(1L)).thenReturn(null);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.getTaskById(1L));
        assertTrue(exception.getMessage().contains("task.not.found"));
    }

    @Test
    void testGetTaskById_Success() {
        when(taskRepository.findTaskByIdWithUsers(1L)).thenReturn(sampleTask);
        when(taskMapper.toDto(sampleTask)).thenReturn(sampleTaskDto);
        when(commentRepository.findCommentsByTaskIdWithAuthor(1L)).thenReturn(sampleComments);

        // Симулируем маппинг комментариев
        when(commentMapper.listToDtoList(sampleComments)).thenReturn(
                sampleComments.stream().map(c -> {
                    CommentDto dto = new CommentDto();
                    dto.setId(c.getId());
                    dto.setContent(c.getContent());
                    UserDto ud = new UserDto();
                    ud.setId(c.getAuthor().getId());
                    ud.setEmail(c.getAuthor().getEmail());
                    dto.setAuthor(ud);
                    return dto;
                }).collect(Collectors.toList())
        );

        TaskDto result = taskService.getTaskById(1L);
        assertNotNull(result);
        assertEquals(sampleTaskDto.getId(), result.getId());
        assertEquals(1, result.getComments().size());
    }

    @Test
    void testDeleteTaskById_TaskNotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.deleteTaskById(1L));
        assertTrue(exception.getMessage().contains("task.not.found"));
    }

    @Test
    void testDeleteTaskById_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTaskById(1L);

        verify(commentRepository, times(1)).deleteCommentsByTaskId(1L);
        verify(taskRepository, times(1)).deleteTaskById(1L);
    }

    @Test
    void testChangeTaskStatus_TaskNotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);
        Principal principal = () -> "user@example.com";
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.changeTaskStatus(1L, Status.IN_PROGRESS, principal));
        assertTrue(exception.getMessage().contains("task.not.found"));
    }

    @Test
    void testChangeTaskStatus_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        Principal principal = () -> "user@example.com";
        taskService.changeTaskStatus(1L, Status.IN_PROGRESS, principal);
        verify(taskRepository, times(1)).updateTaskStatus(1L, Status.IN_PROGRESS);
    }

    @Test
    void testGetAllTasks() {
        Page<Task> page = new PageImpl<>(List.of(sampleTask));
        when(taskRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(taskMapper.listToDtoList(List.of(sampleTask))).thenReturn(List.of(sampleTaskDto));

        CommentProjection projection = new CommentProjection() {
            @Override
            public Long getId() { return 1L; }
            @Override
            public String getContent() { return "Test Comment"; }
            @Override
            public String getAuthorId() { return "1"; }
            @Override
            public String getAuthorEmail() { return "user@example.com"; }
            @Override
            public Long getTaskId() { return 1L; }
        };
        when(commentRepository.findByTaskIdIn(List.of(1L))).thenReturn(List.of(projection));

        List<TaskDto> tasks = taskService.getAllTasks(PageRequest.of(0, 10), new TaskFilter("В ожидании", "Средняя", null, null));
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals(1, tasks.get(0).getComments().size());
    }
}
