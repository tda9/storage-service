package org.example.daiam.annotation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.daiam.infrastruture.support.Scope;


public class ScopeValidator implements ConstraintValidator<ValidScope, Scope> {
    @Override
    public boolean isValid(Scope value, ConstraintValidatorContext context) {
        try {
            Scope.valueOf(value.name().toUpperCase()); // Check if the value is valid
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
