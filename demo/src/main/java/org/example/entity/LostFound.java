package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("lost_found")
public class LostFound {
    @TableId(value = "lost_id", type = IdType.AUTO)
    private Long lostId;
    private Long userId;
    private Integer type; // 1寻物 2拾物
    private String goodsName;
    private String address;
    private LocalDateTime happenTime;
    private String goodsDesc;
    private String imgUrls;
    private Integer status;
    @TableLogic
    private Integer isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}