package com.codepresso.codepresso.controller.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * String -> CheckField 컨버터.
 * - @RequestParam, @PathVariable 바인딩 시 대소문자/공백을 허용.
 */
@Component
public class CheckFieldConverter implements Converter<String, CheckField> {
    @Override
    public CheckField convert(String source) {
        return CheckField.from(source);
    }
}

