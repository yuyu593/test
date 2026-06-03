package org.example.dto;

import lombok.Data;

@Data
public class CollectDto {
    private Long userId;
    private Long targetId;
    private Integer targetType;
}