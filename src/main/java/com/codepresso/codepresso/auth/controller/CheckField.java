package com.codepresso.codepresso.auth.controller;

/**
 * 중복체크 대상 필드 Enum.
 * - 외부 파라미터는 대소문자/공백을 허용하되, 내부적으로 정규화(lowercase).
 */
public enum CheckField {
    ID,
    NICKNAME,
    EMAIL;

    public static CheckField from(String value) {
        if (value == null) throw new IllegalArgumentException("field 파라미터가 필요합니다.");
        return switch (value.trim().toLowerCase()) {
            case "id" -> ID;
            case "nickname" -> NICKNAME;
            case "email" -> EMAIL;
            default -> throw new IllegalArgumentException("지원하지 않는 필드입니다: " + value);
        };
    }

    /** 응답/로깅 등에 사용할 소문자 키 */
    public String asKey() {
        return switch (this) {
            case ID -> "id";
            case NICKNAME -> "nickname";
            case EMAIL -> "email";
        };
    }
}

