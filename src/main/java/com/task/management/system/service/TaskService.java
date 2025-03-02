package com.task.management.system.service;

import com.task.management.system.enums.Priority;
import com.task.management.system.enums.Status;
import com.task.management.system.exception.EntityNotFoundException;
import com.task.management.system.exception.ServiceException;
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
import com.task.management.system.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final TaskMapper taskMapper;
    private final CommentMapper commentMapper;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void createTask(TaskDto taskDto) {
        try {
            Task task = taskMapper.toEntity(taskDto);
            taskRepository.save(task);
            log.info("Successfully created task with ID: {}", task.getId());
        } catch (RuntimeException e) {
            log.error("Unexpected error while creating task", e);
            throw new ServiceException("task.create.error", e);
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @taskSecurity.isAuthor(authentication.name)")
    public void editTask(TaskDto taskDto) {
        Task taskFromDb = taskRepository.findTaskByIdWithUsers(taskDto.getId());
        if (taskFromDb == null) {
            log.warn("Task not found for editing with ID: {}", taskDto.getId());
            throw new EntityNotFoundException("task.not.found", taskDto.getId());
        }
        User assignee = null;
        if (taskDto.getAssignee() != null &&
                (taskFromDb.getAssignee() == null || !taskFromDb.getAssignee().getId().equals(taskDto.getAssignee().getId()))) {
            assignee = userRepository.findById(taskDto.getAssignee().getId())
                    .orElseThrow(() -> new EntityNotFoundException("user.not.found", taskDto.getAssignee().getId()));
        }
        try {
            taskFromDb.setTitle(taskDto.getTitle());
            taskFromDb.setDescription(taskDto.getDescription());
            taskFromDb.setPriority(Priority.fromDisplayName(taskDto.getPriority()));
            taskFromDb.setStatus(Status.fromDisplayName(taskDto.getStatus()));
            taskFromDb.setAssignee(assignee);

            taskRepository.save(taskFromDb);
            log.info("Successfully updated task with ID: {}", taskDto.getId());
        } catch (RuntimeException e) {
            log.error("Unexpected error while updating task", e);
            throw new ServiceException("task.edit.error", e);
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findTaskByIdWithUsers(taskId);
        if (task == null) {
            log.warn("Task not found with ID: {}", taskId);
            throw new EntityNotFoundException("task.not.found", taskId);
        }
        try {
            TaskDto taskDto = taskMapper.toDto(task);
            List<Comment> comments = commentRepository.findCommentsByTaskIdWithAuthor(taskId);
            taskDto.setComments(commentMapper.listToDtoList(comments));

            log.info("Successfully retrieved task with ID: {}", taskId);
            return taskDto;
        } catch (RuntimeException e) {
            log.error("Unexpected error while retrieving task", e);
            throw new ServiceException("task.get.error", e);
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @taskSecurity.isAuthor(authentication.name)")
    public void deleteTaskById(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            log.warn("Task not found for deletion with ID: {}", taskId);
            throw new EntityNotFoundException("task.not.found", taskId);
        }
        try {
            commentRepository.deleteCommentsByTaskId(taskId);
            taskRepository.deleteTaskById(taskId);
            log.info("Successfully deleted task with ID: {}", taskId);

        } catch (RuntimeException e) {
            log.error("Unexpected error while deleting task", e);
            throw new ServiceException("task.delete.error", e);
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and taskSecurity.isAssignee(principal.name)")
    public void changeTaskStatus(Long taskId, Status status, Principal principal) {
        if (!taskRepository.existsById(taskId)) {
            log.warn("Task not found for status change with ID: {}", taskId);
            throw new EntityNotFoundException("task.not.found", taskId);
        }
        try {
            taskRepository.updateTaskStatus(taskId, Status.fromDisplayName(status.getDisplayName()));
            log.info("Successfully updated status for task ID: {}", taskId);
        } catch (RuntimeException e) {
            log.error("Unexpected error while changing task status", e);
            throw new ServiceException("task.status.change.error", e);
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<TaskDto> getAllTasks(Pageable pageable, TaskFilter taskFilter) {
            Specification<Task> specification = withFilters(taskFilter);
            List<TaskDto> taskList = taskMapper.listToDtoList(taskRepository.findAll(specification, pageable).getContent());

            List<Long> taskIds = taskList.stream()
                    .map(TaskDto::getId)
                    .toList();

            List<CommentProjection> comments = commentRepository.findByTaskIdIn(taskIds);

            Map<Long, List<CommentProjection>> commentsByTask = comments.stream()
                    .collect(Collectors.groupingBy(CommentProjection::getTaskId));

            return taskList.stream()
                    .peek(task -> task.setComments(
                            commentsByTask.getOrDefault(task.getId(), Collections.emptyList()).stream()
                                    .map(proj -> new CommentDto(
                                            proj.getId(),
                                            proj.getContent(),
                                            new UserDto(proj.getId(), proj.getAuthorEmail())
                                    ))
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());
    }

    public Specification<Task> withFilters(TaskFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), Status.fromDisplayName(filter.getStatus())));
            }
            if (filter.getPriority() != null) {
                predicates.add(cb.equal(root.get("priority"), Priority.fromDisplayName(filter.getPriority())));
            }
            if (filter.getAuthorEmail() != null) {
                predicates.add(cb.equal(root.get("author").get("email"), filter.getAuthorEmail()));
            }
            if (filter.getAssigneeEmail() != null) {
                predicates.add(cb.equal(root.get("assignee").get("email"), filter.getAssigneeEmail()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
