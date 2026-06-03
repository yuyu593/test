package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("second_hand")
public class SecondHand {
    @TableId(value = "second_id", type = IdType.AUTO)
    private Long secondId;
    private Long userId;
    private String goodsName;
    private String goodsType;
    private Integer quality;
    private Double price;
    private String goodsDesc;
    private String imgUrls;
    private String tradeAddress;
    private Integer status;
    @TableLogic
    private Integer isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}