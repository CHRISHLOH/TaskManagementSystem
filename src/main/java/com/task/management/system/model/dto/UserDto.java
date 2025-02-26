package com.task.management.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;
    @NotNull
    @Schema(description = "Почта пользователя", example = "admin@example.com")
    private String email;
}
