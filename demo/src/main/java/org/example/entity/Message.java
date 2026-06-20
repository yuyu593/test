package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName message
 */
@TableName(value ="message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long messageId;

    private Long userId;

    private Long senderUserId;

    private String itemType;

    private Long itemId;

    private String itemTitle;

    private Integer type;

    private String title;

    private String content;

    private Integer isRead;

    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
