package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("campus_news")
public class CampusNews {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long publishId;
    private String title;
    private String content;
    private String imgUrls;
    private Integer type; // 1校园通知 2社团活动 3生活贴士 4校友动态
    private Integer likeNum = 0;
    private Integer status;
    @TableLogic
    private Integer isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}