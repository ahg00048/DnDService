package es.ujaen.ahg00048.microservice_characterSheet.qualifier;

import es.ujaen.ahg00048.microservice_characterSheet.qualifier.validator.DiceRollValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Constraint(validatedBy = DiceRollValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface DiceRoll {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
