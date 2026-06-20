package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.Result;
import org.example.entity.Message;
import org.example.mapper.MessageMapper;
import org.example.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public Result<List<Message>> getByUserId(Long userId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        // 系统通知(type=1)的 sender_user_id 是对方用户ID（用于联系对方），
        // 不是"我发的消息"，所以 type=1 只按 user_id 匹配
        wrapper.and(w -> w.eq("user_id", userId).or(i -> i.ne("type", 1).eq("sender_user_id", userId)));
        wrapper.orderByDesc("create_time");
        List<Message> messages = baseMapper.selectList(wrapper);
        return Result.success(messages);
    }

    @Override
    public Result<Message> getById(Long messageId) {
        Message message = baseMapper.selectById(messageId);
        return Result.success(message);
    }

    @Override
    public Result<Void> markAsRead(Long messageId) {
        Message message = baseMapper.selectById(messageId);
        if (message == null) {
            return Result.fail("消息不存在");
        }
        message.setIsRead(1);
        baseMapper.updateById(message);
        return Result.success();
    }

    @Override
    public Result<Void> markAllAsRead(Long userId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("is_read", 0);
        List<Message> messages = baseMapper.selectList(wrapper);
        for (Message message : messages) {
            message.setIsRead(1);
            baseMapper.updateById(message);
        }
        return Result.success();
    }

    @Override
    public Result<Integer> getUnreadCount(Long userId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("is_read", 0);
        int count = (int) (long) baseMapper.selectCount(wrapper);
        return Result.success(count);
    }

    @Override
    public Result<Void> markConversationAsRead(Long userId, Long otherUserId) {
        try {
            QueryWrapper<Message> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            wrapper.eq("sender_user_id", otherUserId);
            wrapper.eq("is_read", 0);
            List<Message> list = baseMapper.selectList(wrapper);
            if (list != null && list.size() > 0) {
                for (Message msg : list) {
                    msg.setIsRead(1);
                    baseMapper.updateById(msg);
                }
            }
            return Result.success();
        } catch (Exception e) {
            return Result.fail("标记已读失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Message>> getConversation(Long userId, Long otherUserId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w.eq("user_id", userId).eq("sender_user_id", otherUserId))
               .or(w -> w.eq("user_id", otherUserId).eq("sender_user_id", userId));
        wrapper.orderByAsc("create_time");
        List<Message> list = baseMapper.selectList(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<Message> sendMessage(Long senderUserId, Long receiverUserId, Integer type, String title, String content, String itemType, Long itemId, String itemTitle) {
        return sendMessage(senderUserId, receiverUserId, type, title, content, itemType, itemId, itemTitle, null);
    }

    @Override
    public Result<Message> sendMessage(Long senderUserId, Long receiverUserId, Integer type, String title, String content, String itemType, Long itemId, String itemTitle, String createTimeStr) {
        if (receiverUserId == null) {
            return Result.fail("接收者ID不能为空");
        }
        if (title == null || title.trim().isEmpty()) {
            return Result.fail("标题不能为空");
        }
        String dbTimeStr;
        if (createTimeStr != null && !createTimeStr.isEmpty() && createTimeStr.length() >= 19) {
            dbTimeStr = createTimeStr.substring(0, 19).replace('T', ' ');
        } else {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dbTimeStr = sdf.format(new java.util.Date());
        }

        Message message = new Message();
        message.setSenderUserId(senderUserId);
        message.setUserId(receiverUserId);
        message.setItemType(itemType);
        message.setItemId(itemId);
        message.setItemTitle(itemTitle);
        message.setType(type != null ? type : 2);
        message.setTitle(title);
        message.setContent(content == null ? "" : content);
        message.setIsRead(0);

        baseMapper.insertWithTime(message, dbTimeStr);

        Message saved = baseMapper.selectById(message.getMessageId());
        return saved != null ? Result.success(saved) : Result.fail("发送失败");
    }

    // ========== Map 返回版本（createTime 以字符串 "yyyy-MM-dd HH:mm:ss" 返回）==========

    @Override
    public Result<List<Map<String, Object>>> getConversationAsMap(Long userId, Long otherUserId) {
        List<Map<String, Object>> list = baseMapper.selectConversationAsMap(userId, otherUserId);
        return Result.success(list);
    }

    @Override
    public Result<List<Map<String, Object>>> getByUserIdAsMap(Long userId) {
        List<Map<String, Object>> list = baseMapper.selectByUserIdAsMap(userId);
        return Result.success(list);
    }

    @Override
    public Result<Map<String, Object>> getByIdAsMap(Long messageId) {
        Map<String, Object> map = baseMapper.selectByIdAsMap(messageId);
        return Result.success(map);
    }

    @Override
    public Result<Map<String, Object>> sendMessageReturnMap(Long senderUserId, Long receiverUserId, Integer type, String title, String content, String itemType, Long itemId, String itemTitle, String createTimeStr) {
        if (receiverUserId == null) {
            return Result.fail("接收者ID不能为空");
        }
        if (title == null || title.trim().isEmpty()) {
            return Result.fail("标题不能为空");
        }
        // 前端传 "2026-06-20T10:30:45.000"，截成 "2026-06-20 10:30:45" 直接当字面量写入 MySQL datetime
        String dbTimeStr;
        if (createTimeStr != null && !createTimeStr.isEmpty() && createTimeStr.length() >= 19) {
            dbTimeStr = createTimeStr.substring(0, 19).replace('T', ' ');
        } else {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dbTimeStr = sdf.format(new java.util.Date());
        }

        Message message = new Message();
        message.setSenderUserId(senderUserId);
        message.setUserId(receiverUserId);
        message.setItemType(itemType);
        message.setItemId(itemId);
        message.setItemTitle(itemTitle);
        message.setType(type != null ? type : 2);
        message.setTitle(title);
        message.setContent(content == null ? "" : content);
        message.setIsRead(0);

        baseMapper.insertWithTime(message, dbTimeStr);

        Map<String, Object> saved = baseMapper.selectByIdAsMap(message.getMessageId());
        return saved != null ? Result.success(saved) : Result.fail("发送失败");
    }
}
