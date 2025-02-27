package com.task.management.system.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;

import com.task.management.system.exception.EntityNotFoundException;
import com.task.management.system.exception.ServiceException;
import com.task.management.system.mapper.CommentMapper;
import com.task.management.system.model.dto.CreateCommentDto;
import com.task.management.system.model.entity.Comment;
import com.task.management.system.model.entity.Task;
import com.task.management.system.model.entity.User;
import com.task.management.system.repository.CommentRepository;
import com.task.management.system.repository.TaskRepository;
import com.task.management.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CommentService commentService;

    private CreateCommentDto createCommentDto;
    private User sampleUser;
    private Task sampleTask;
    private Comment sampleComment;
    private Principal principal;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("user@example.com");
        sampleUser.setPassword("password");

        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Test Task");

        createCommentDto = new CreateCommentDto();
        createCommentDto.setContent("Test comment");
        createCommentDto.setTaskId(1L);

        sampleComment = new Comment();
        sampleComment.setId(1L);
        sampleComment.setContent("Test comment");

        principal = () -> "user@example.com";
    }

    @Test
    void testAddCommentSuccess() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(sampleUser);
        when(taskRepository.getReferenceById(1L)).thenReturn(sampleTask);
        when(commentMapper.createDtoToEntity(createCommentDto)).thenReturn(sampleComment);

        commentService.addComment(createCommentDto, principal);

        verify(commentRepository, times(1)).save(sampleComment);
        assertEquals(sampleUser, sampleComment.getAuthor());
        assertEquals(sampleTask, sampleComment.getTask());
    }

    @Test
    void testAddCommentUserNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.addComment(createCommentDto, principal));
        assertEquals("user.not.found", exception.getMessage());
    }

    @Test
    void testAddCommentRuntimeException() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(sampleUser);
        when(taskRepository.getReferenceById(1L)).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> commentService.addComment(createCommentDto, principal));
        assertTrue(exception.getMessage().contains("user.nor.assignee"));
    }
}
