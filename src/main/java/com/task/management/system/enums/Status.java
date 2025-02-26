package com.task.management.system.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Status {
    PENDING("В ожидании"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершено");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public static Status fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(p -> p.displayName.equals(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown priority: " + displayName));
    }
}
