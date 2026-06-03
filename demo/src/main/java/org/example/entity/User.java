package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(value = "userid", type = IdType.AUTO)
    private Long userId;
    private String studentNo;
    private String nickName;
    private String password;
    private String major;
    private String dormNo;
    private String avatar;
    private Integer creditScore = 100;
    private String phone;
    @TableLogic
    private Integer isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}