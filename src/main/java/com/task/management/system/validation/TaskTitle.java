package com.task.management.system.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@NotBlank(message = "{task.title.blank}")
@Size(max = 255, message = "{task.title.size}")
@Pattern(regexp = "^[A-Za-z0-9\\s]+$", message = "{task.title.pattern}")
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface TaskTitle {
    String message() default "{task.title.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}