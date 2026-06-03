package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("purchase_info")
public class PurchaseInfo {
    @TableId(value = "purchase_id", type = IdType.AUTO)
    private Long purchaseId;

    private Long userId;
    private String title;
    private String content;
    private String category;
    private Double price;
    private String contact;
    private Integer status;
    private Integer isUrgent;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}