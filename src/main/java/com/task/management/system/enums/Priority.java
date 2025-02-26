package com.task.management.system.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Priority {
    HIGH("Высокая"),
    MEDIUM("Средняя"),
    LOW("Низкая");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }

    public static Priority fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(p -> p.displayName.equals(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown priority: " + displayName));
    }
}