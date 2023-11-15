package com.caipiao.common.validator.annotation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.regex.Pattern;


@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = {MobileValidator.MobileValidatorClass.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface MobileValidator {

    /**
     * 错误提示信息，可以写死,也可以填写国际化的key
     */
    String message() default "手机号码不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String regexp() default "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";

    class MobileValidatorClass implements ConstraintValidator<MobileValidator, String> {

        /**
         * 手机验证规则
         */
        private Pattern pattern;

        @Override
        public void initialize(MobileValidator mobile) {
            pattern = Pattern.compile(mobile.regexp());
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            return pattern.matcher(value).matches();
        }
    }
}
