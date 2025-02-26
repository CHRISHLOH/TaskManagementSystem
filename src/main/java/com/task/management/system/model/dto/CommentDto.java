package com.task.management.system.model.dto;

import com.task.management.system.validation.CommentContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для отображения комментария")
public class CommentDto {

    @Schema(description = "Уникальный идентификатор комментария", example = "1")
    private Long id;

    @Schema(description = "Содержимое комментария", example = "Комментарий")
    @CommentContent
    private String content;

    @Schema(description = "Информация об авторе комментария")
    private UserDto author;
}
