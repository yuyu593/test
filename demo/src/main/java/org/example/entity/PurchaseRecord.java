package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("purchase_record")
public class PurchaseRecord {
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;
    private Long userId;
    private Long sellerId;
    private Long secondId;
    private String goodsName;
    private String goodsType;
    private Double price;
    private String imgUrls;
    private Integer status;
    private LocalDateTime payTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}