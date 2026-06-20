package org.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublishVO {
    private Long targetId;
    private Integer targetType; //1闲置 2求购 3动态 4失物
    private String title;
    private String content;
    private LocalDateTime createTime;
}