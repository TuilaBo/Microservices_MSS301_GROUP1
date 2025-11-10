package com.khoavdse170395.accountservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FptEmailValidator implements ConstraintValidator<FptEmail, String> {

    private static final String FPT_DOMAIN = "@fpt.edu.vn";

    @Override
    public void initialize(FptEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.toLowerCase().endsWith(FPT_DOMAIN);
    }
}
