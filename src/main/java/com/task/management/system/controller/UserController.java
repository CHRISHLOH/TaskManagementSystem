package com.task.management.system.controller;

import com.task.management.system.enums.Status;

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
public class UserController {
    private final TaskService taskService;
    private final CommentService commentService;

    @Operation(summary = "Получить задачу по ID", description = "Возвращает задачу по её уникальному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @GetMapping("/task/{id}")
    public ResponseEntity<TaskDto> getUserTask(@PathVariable("id") Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(summary = "Создать задачу",
            description = "Добавляет новую задачу в систему")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Задача создана",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = String.class)
            )}
    )})
    @PostMapping("/task/create")
    public ResponseEntity<String> createUserTask(@Valid  @RequestBody TaskDto taskDto) {
        taskService.createTask(taskDto);
        return ResponseEntity.ok("Задача создана");
    }

    @Operation(summary = "Обновить задачу",
            description = "Обновляет данные существующей задачи")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Задача обновлена",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = String.class)
            )}
    )})
    @PutMapping("/task/edit")
    public ResponseEntity<String> updateUserTask(@Valid @RequestBody TaskDto taskDto) {
        taskService.editTask(taskDto);
        return ResponseEntity.ok("Задача обновлена");
    }

    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача удалена",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = String.class)
                    )}),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @DeleteMapping("/task/{id}")
    public ResponseEntity<String> deleteUserTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.ok("Задача удалена");
    }

    @Operation(summary = "Получить все задачи пользователя",
            description = "Возвращает список всех задач пользователя с поддержкой пагинации и фильтрации")
    @Parameters({
            @Parameter(name = "page", description = "Номер страницы", example = "0"),
            @Parameter(name = "size", description = "Размер страницы", example = "10"),
            @Parameter(name = "status",
                    description = "Статус задачи",
                    schema = @Schema(type = "string", allowableValues = {"В ожидании", "В процессе", "Завершено"})),
            @Parameter(name = "priority",
                    description = "Приоритет задачи",
                    schema = @Schema(type = "string", allowableValues = {"Высокая", "Средняя", "Низкая"})),
            @Parameter(name = "assignee", description = "Исполнитель задачи", example = "user@mail.ru")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач успешно возвращен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TaskDto.class))))
    })
    @GetMapping("/tasks/user/all")
    public ResponseEntity<List<TaskDto>> getAllTasks(
            Principal principal,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "assignee", required = false) String assignee

    ) {
        Pageable pageable = PageRequest.of(page, size);
        TaskFilter taskFilter = new TaskFilter(status, priority, principal.getName(), assignee);
        return ResponseEntity.ok(taskService.getAllTasks(pageable, taskFilter));
    }

    @Operation(
            summary = "Добавить комментарий",
            description = "Добавляет новый комментарий к задаче. Комментарий не может быть пустым, taskId обязательное поле."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий добавлен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    @PostMapping("/comment/create")
    public ResponseEntity<String> addComment(@Valid @RequestBody CreateCommentDto commentDto, Principal principal) {
        commentService.addComment(commentDto, principal);
        return ResponseEntity.ok("Комментарий добавлен");
    }

    @Operation(
            summary = "Изменить статус задачи",
            description = "Обновляет статус задачи для текущего пользователя. Ожидается JSON объект вида { \"status\": \"В процессе\" }."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус изменен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    @PutMapping("/tasks/{id}/status")
    public ResponseEntity<String> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        taskService.changeTaskStatus(id, Status.fromDisplayName(requestBody.get("status")));
        return ResponseEntity.ok("Статус изменен");
    }

    @Operation(summary = "Получить все задачи",
            description = "Возвращает список всех задач с поддержкой пагинации и фильтрации")
    @Parameters({
            @Parameter(name = "page", description = "Номер страницы", example = "0"),
            @Parameter(name = "size", description = "Размер страницы", example = "10"),
            @Parameter(name = "status",
                    description = "Статус задачи",
                    schema = @Schema(type = "string", allowableValues = {"В ожидании", "В процессе", "Завершено"})),
            @Parameter(name = "priority",
                    description = "Приоритет задачи",
                    schema = @Schema(type = "string", allowableValues = {"Высокая", "Средняя", "Низкая"})),
            @Parameter(name = "author", description = "Автор задачи", example = "user@mail.ru"),
            @Parameter(name = "assignee", description = "Исполнитель задачи", example = "user@mail.ru")
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
}
