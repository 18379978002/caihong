package com.caipiao.common.validator.annotation;

import cn.hutool.core.util.IdcardUtil;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IdCardValidator.IdCardValidatorClass.class)
public @interface IdCardValidator {

    String message() default "身份证号码不合法";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class IdCardValidatorClass implements ConstraintValidator<IdCardValidator, String> {

        @Override
        public void initialize(IdCardValidator identityCardNumber) {
        }

        @Override
        public boolean isValid(String idCard, ConstraintValidatorContext constraintValidatorContext) {
            return IdcardUtil.isValidCard(idCard);
        }
    }
}

