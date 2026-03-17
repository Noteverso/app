package com.noteverso.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ContentRequiredValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentRequired {
    String message() default "Either content or contentJson is required";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
