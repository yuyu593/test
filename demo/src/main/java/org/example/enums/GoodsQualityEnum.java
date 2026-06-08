package org.example.enums;

import lombok.Getter;

@Getter
public enum GoodsQualityEnum {
    NEW(1, "全新"),
    NINETY_NEW(2, "99新"),
    EIGHTY_NEW(3, "9成新"),
    SEVENTY_NEW(4, "8成新"),
    ORDINARY(5, "一般"),
    POOR(6, "较差");

    private final Integer code;
    private final String desc;

    GoodsQualityEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (GoodsQualityEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "未知";
    }
}