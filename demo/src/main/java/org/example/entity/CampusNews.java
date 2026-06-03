package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("campus_news")
public class CampusNews {

    @TableId(value = "news_id", type = IdType.AUTO)
    private Long newsId;

    private Long publishId;
    private String title;
    private String content;
    private String imgUrls;
    private Integer type;
    private Integer likeNum;
    private Integer status;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}