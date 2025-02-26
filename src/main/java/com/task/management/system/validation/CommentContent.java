package com.task.management.system.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@NotBlank(message = "{comment.content.blank}")
@Size(max = 250, message = "{comment.content.size}")
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface CommentContent {
    String message() default "{comment.content.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
