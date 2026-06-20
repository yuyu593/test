package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback {
    @TableId(value = "feedback_id", type = IdType.AUTO)
    private Integer feedbackId;
    private Long userId;
    private String type;
    private String content;
    private String contact;
    private Integer status;
    private String reply;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}