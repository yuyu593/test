package org.example.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {
    PENDING(0, "待审核"),
    PUBLISHED(1, "已发布"),
    TRADED(2, "已交易"),
    OFF_SHELF(3, "已下架"),
    FOUND(2, "已找回");

    private final Integer code;
    private final String desc;

    StatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (StatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知";
    }
}