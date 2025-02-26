package com.task.management.system.controller;

import com.task.management.system.enums.Priority;
import com.task.management.system.enums.Status;
import com.task.management.system.model.dto.CommentDto;
import com.task.management.system.model.dto.CreateCommentDto;
import com.task.management.system.model.dto.TaskDto;
import com.task.management.system.model.dto.TaskFilter;
import com.task.management.system.service.CommentService;
import com.task.management.system.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Admin Controller", description = "Контроллер для административных операций")
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TaskService taskService;
    private final CommentService commentService;

    public AdminController(TaskService taskService, CommentService commentService) {
        this.taskService = taskService;
        this.commentService = commentService;
    }

    @Operation(summary = "Получить задачу по ID", description = "Возвращает задачу по её уникальному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @GetMapping("task/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(summary = "Получить все задачи",
            description = "Возвращает список всех задач с поддержкой пагинации и фильтрации")
    @Parameters({
            @Parameter(name = "page", description = "Номер страницы", example = "0"),
            @Parameter(name = "size", description = "Размер страницы", example = "10"),
            @Parameter(name = "status", description = "Статус задачи", schema = @Schema(implementation = Status.class)),
            @Parameter(name = "priority", description = "Приоритет задачи", schema = @Schema(implementation = Priority.class)),
            @Parameter(name = "author", description = "Автор задачи", example = "user1"),
            @Parameter(name = "assignee", description = "Исполнитель задачи", example = "user2")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач успешно возвращен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TaskDto.class))))
    })
    @GetMapping("/task/all")
    public ResponseEntity<List<TaskDto>> getAllTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "assignee", required = false) String assignee
    ) {
        Pageable pageable = PageRequest.of(page, size);
        TaskFilter taskFilter = new TaskFilter(status, priority, author, assignee);
        return ResponseEntity.ok(taskService.getAllTasks(pageable, taskFilter));
    }

    @Operation(summary = "Обновить задачу",
            description = "Обновляет данные существующей задачи")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Задача создана",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TaskDto.class)
            )}
    )})
    @PutMapping("/task/edit")
    public ResponseEntity<String> updateTask(@Valid @RequestBody TaskDto taskDto) {
        taskService.editTask(taskDto);
        return ResponseEntity.ok("Задача обновлена");
    }

    @Operation(summary = "Создать задачу",
            description = "Добавляет новую задачу в систему")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Задача создана",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TaskDto.class)
            )}
    )})
    @PostMapping("/task/create")
    public ResponseEntity<String> addTask(@Valid @RequestBody TaskDto taskDto) {
        log.info(taskDto.toString());
        taskService.createTask(taskDto);
        return ResponseEntity.ok("Задача создана");
    }

    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача удалена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @DeleteMapping("/task/delete/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.ok("Задача удалена");
    }

    @Operation(summary = "Добавить комментарий", description = "Добавляет комментарий к задаче")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Комментарий добавлен",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TaskDto.class)
            )}
    )})
    @PostMapping("/comment/create")
    public ResponseEntity<String> addComment(@Valid @RequestBody CreateCommentDto commentDto, Principal principal) {
        commentService.addComment(commentDto, principal);
        return ResponseEntity.ok("Saved");
    }
}
