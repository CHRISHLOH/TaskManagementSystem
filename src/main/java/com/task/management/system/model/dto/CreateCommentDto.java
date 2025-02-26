package com.task.management.system.model.dto;

import com.task.management.system.validation.CommentContent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для создания нового комментария")
public class CreateCommentDto {

    @Schema(description = "Содержимое комментария", example = "Это новый комментарий")
    @CommentContent
    private String content;

    @Schema(description = "Идентификатор задачи, к которой привязан комментарий", example = "1")
    @NotNull(message = "Task ID is required")
    private Long taskId;
}
