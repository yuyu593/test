package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    // createTimeStr 直接传 "yyyy-MM-dd HH:mm:ss" 字符串，由 MySQL 解析为 datetime，绕过 JVM 时区
    @Insert("INSERT INTO message (user_id, sender_user_id, item_type, item_id, item_title, type, title, content, is_read, create_time) " +
            "VALUES (#{msg.userId}, #{msg.senderUserId}, #{msg.itemType}, #{msg.itemId}, #{msg.itemTitle}, " +
            "#{msg.type}, #{msg.title}, #{msg.content}, #{msg.isRead}, #{createTimeStr})")
    @Options(useGeneratedKeys = true, keyProperty = "msg.messageId", keyColumn = "message_id")
    int insertWithTime(@Param("msg") Message msg, @Param("createTimeStr") String createTimeStr);

    // 查询会话消息，并把 create_time 直接作为字符串返回，避免 Date 时区转换
    @Select("SELECT message_id, user_id, sender_user_id, item_type, item_id, item_title, type, title, content, is_read, " +
            "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') AS create_time_str " +
            "FROM message " +
            "WHERE (user_id = #{userId} AND sender_user_id = #{otherUserId}) " +
            "   OR (user_id = #{otherUserId} AND sender_user_id = #{userId}) " +
            "ORDER BY create_time ASC")
    List<Map<String, Object>> selectConversationAsMap(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);

    // 查询用户全部消息，并把 create_time 直接作为字符串返回
    // 注意：系统通知(type=1)的 sender_user_id 存的是对方用户ID（用于联系对方），
    //      不是"我发的消息"，所以 type=1 的消息只按 user_id（接收者）匹配
    @Select("SELECT message_id, user_id, sender_user_id, item_type, item_id, item_title, type, title, content, is_read, " +
            "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') AS create_time_str " +
            "FROM message " +
            "WHERE user_id = #{userId} OR (type != 1 AND sender_user_id = #{userId}) " +
            "ORDER BY create_time DESC")
    List<Map<String, Object>> selectByUserIdAsMap(@Param("userId") Long userId);

    // 按 ID 查询，并把 create_time 直接作为字符串返回
    @Select("SELECT message_id, user_id, sender_user_id, item_type, item_id, item_title, type, title, content, is_read, " +
            "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') AS create_time_str " +
            "FROM message WHERE message_id = #{messageId}")
    Map<String, Object> selectByIdAsMap(@Param("messageId") Long messageId);
}
