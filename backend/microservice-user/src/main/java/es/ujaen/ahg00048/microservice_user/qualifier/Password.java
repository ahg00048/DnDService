package es.ujaen.ahg00048.microservice_user.qualifier;

import es.ujaen.ahg00048.microservice_user.qualifier.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Password {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


