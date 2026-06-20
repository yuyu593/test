package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.Result;
import org.example.entity.Message;

import java.util.List;
import java.util.Map;

public interface MessageService extends IService<Message> {
    Result<List<Message>> getByUserId(Long userId);
    Result<Message> getById(Long messageId);
    Result<Void> markAsRead(Long messageId);
    Result<Void> markAllAsRead(Long userId);
    Result<Void> markConversationAsRead(Long userId, Long otherUserId);
    Result<Integer> getUnreadCount(Long userId);
    Result<Message> sendMessage(Long senderUserId, Long receiverUserId, Integer type, String title, String content, String itemType, Long itemId, String itemTitle);
    Result<Message> sendMessage(Long senderUserId, Long receiverUserId, Integer type, String title, String content, String itemType, Long itemId, String itemTitle, String createTime);
    Result<List<Message>> getConversation(Long userId, Long otherUserId);

    // 返回 Map 形式，createTime 以字符串 "yyyy-MM-dd HH:mm:ss" 返回，避免 Date 时区转换
    Result<List<Map<String, Object>>> getConversationAsMap(Long userId, Long otherUserId);
    Result<List<Map<String, Object>>> getByUserIdAsMap(Long userId);
    Result<Map<String, Object>> getByIdAsMap(Long messageId);
    // 发送消息并返回 Map，createTime 以字符串返回
    Result<Map<String, Object>> sendMessageReturnMap(Long senderUserId, Long receiverUserId, Integer type, String title, String content, String itemType, Long itemId, String itemTitle, String createTime);
}
