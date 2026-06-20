package org.example.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CollectVo {
    private Long collectId;
    private Long userId;
    private Long targetId;
    private Integer targetType;
    private LocalDateTime collectTime;

    // 关联出来的正文信息
    private String title;
    private String content;
}