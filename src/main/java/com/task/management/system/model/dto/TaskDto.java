package com.task.management.system.model.dto;

import com.task.management.system.validation.TaskDescription;
import com.task.management.system.validation.TaskTitle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для отображения задачи")
public class TaskDto {

    @Schema(description = "Уникальный идентификатор задачи", example = "1")
    private Long id;

    @Schema(description = "Название задачи", example = "Разработать API для комментариев")
    @TaskTitle
    private String title;

    @Schema(description = "Описание задачи", example = "Необходимо реализовать API для работы с комментариями")
    @TaskDescription
    private String description;

    @Schema(description = "Статус задачи", example = "В процессе", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Status is required")
    private String status;

    @Schema(description = "Приоритет задачи", example = "Высокая", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Priority is required")
    private String priority;

    @Schema(description = "Автор задачи", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Author is required")
    private UserDto author;

    @Schema(description = "Исполнитель задачи (если назначен)")
    private UserDto assignee;

    @Schema(description = "Комментарии к задаче")
    private List<CommentDto> comments;
}
