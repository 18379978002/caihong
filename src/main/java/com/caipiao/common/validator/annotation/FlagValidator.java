package com.caipiao.common.validator.annotation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author: create by xiaoyinandan
 * @version: v1.0
 * @description:
 * @date: 2020/4/2 9:53
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = FlagValidator.FlagValidatorClass.class)
public @interface FlagValidator {

    String[] value() default {};

    String message() default "flag is not found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class FlagValidatorClass implements ConstraintValidator<FlagValidator, Integer> {

        private String[] vaules;

        @Override
        public void initialize(FlagValidator constraintAnnotation) {
            this.vaules = constraintAnnotation.value();
        }

        @Override
        public boolean isValid(Integer value, ConstraintValidatorContext context) {
            boolean isValid = false;
            if(value == null){
                //当状态为空时使用默认值
                return true;
            }
            for(int i=0;i<this.vaules.length;i++){
                if(this.vaules[i].equals(String.valueOf(value))){
                    isValid = true;
                    break;
                }
            }
            return isValid;
        }

    }
}
