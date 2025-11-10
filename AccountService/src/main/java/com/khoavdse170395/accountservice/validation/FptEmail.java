package com.khoavdse170395.accountservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FptEmailValidator.class)
@Documented
public @interface FptEmail {
    String message() default "Email must be from @fpt.edu.vn domain";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
