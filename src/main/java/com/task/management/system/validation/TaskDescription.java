package com.task.management.system.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@NotBlank(message = "{task.description.blank}") // Описание не должно быть пустым
@Size(max = 2000, message = "{task.description.size}") // Максимальная длина 2000 символов
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {}) // Используем стандартные валидаторы
public @interface TaskDescription {
    String message() default "{task.description.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
