package es.ujaen.ahg00048.microservice_characterSheet.qualifier.validator;

import es.ujaen.ahg00048.microservice_characterSheet.qualifier.DiceRoll;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DiceRollValidator implements ConstraintValidator<DiceRoll, String> {
    private final static String regex = "";


    @Override
    public void initialize(DiceRoll constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches(regex);
    }
}
