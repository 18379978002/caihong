package com.caipiao.common.validator.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

import org.apache.commons.beanutils.BeanUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validation annotation to validate that 2 fields have the same value.
 * An array of fields and their matching confirmation fields can be supplied.
 *
 * Example, compare 1 pair of fields:
 * @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match")
 *
 * Example, compare more than 1 pair of fields:
 * @FieldMatch.List({
 *   @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match"),
 *   @FieldMatch(first = "email", second = "confirmEmail", message = "The email fields must match")})
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FieldMatch.FieldMatchValidator.class)
@Documented
public @interface FieldMatch
{
    String message() default "{constraints.fieldmatch}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return The first field
     */
    String first();

    /**
     * @return The second field
     */
    String second();

    /**
     * Defines several <code>@FieldMatch</code> annotations on the same element
     *
     * @see FieldMatch
     */
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        FieldMatch[] value();
    }

    class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
        private String firstFieldName;
        private String secondFieldName;

        @Override
        public void initialize(final FieldMatch constraintAnnotation) {
            firstFieldName = constraintAnnotation.first();
            secondFieldName = constraintAnnotation.second();
        }

        @Override
        public boolean isValid(final Object value, final ConstraintValidatorContext context) {
            try {
                final Object firstObj = BeanUtils.getProperty(value, firstFieldName);
                final Object secondObj = BeanUtils.getProperty(value, secondFieldName);

                return firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
            } catch (final Exception ignore) {
                // ignore
            }
            return true;
        }
    }
}
