package com.task.management.system.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @taskSecurity.isAssignee(authentication.name, #commentDto.taskId))")
    public void addComment(CreateCommentDto commentDto, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if(user == null) {
            throw new EntityNotFoundException("user.not.found");
        }
        try {
            Task task = taskRepository.getReferenceById(commentDto.getTaskId());
            Comment comment = commentMapper.createDtoToEntity(commentDto);
            comment.setAuthor(user);
            comment.setTask(task);
            commentRepository.save(comment);
        } catch (RuntimeException e) {
            log.error("Unexpected error while retrieving task", e);
            throw new ServiceException("user.nor.assignee", e);
        }
    }
}
