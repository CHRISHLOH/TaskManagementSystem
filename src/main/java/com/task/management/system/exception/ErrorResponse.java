package com.task.management.system.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private List<String> errors = new ArrayList<>();
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(String message) {
        this.message = message;
    }
}
