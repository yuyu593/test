package org.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CollectInfo {
    private Long collectId;
    private Long userId;
    private Long targetId;
    private Integer targetType;
    private LocalDateTime collectTime;

    // 目标类型常量，和你业务对应
    public static final int TYPE_SECOND = 1;    // 闲置物品
    public static final int TYPE_PURCHASE = 2;  // 求购信息
    public static final int TYPE_NEWS = 3;     // 校园动态
    public static final int TYPE_LOST = 4;     // 失物招领
}