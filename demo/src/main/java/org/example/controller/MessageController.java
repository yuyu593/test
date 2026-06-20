package org.example.controller;

import org.example.common.Result;
import org.example.entity.User;
import org.example.service.MessageService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private UserService userService;

    // 通用工具：将 MyBatis 下划线 key 转成驼峰（message_id -> messageId）
    private String toCamel(String key) {
        if (key == null || key.indexOf('_') < 0) return key;
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c == '_') {
                upper = true;
            } else {
                sb.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return sb.toString();
    }

    // 将 Map 的下划线 key 转驼峰，并把 create_time_str 作为 createTime 字符串返回
    private Map<String, Object> convertRow(Map<String, Object> row) {
        if (row == null) return null;
        Map<String, Object> out = new HashMap<>();
        String createTime = null;
        for (Map.Entry<String, Object> e : row.entrySet()) {
            String key = e.getKey() == null ? "" : e.getKey().toLowerCase();
            Object val = e.getValue();
            if ("create_time_str".equals(key)) {
                createTime = val == null ? "" : val.toString();
                continue;
            }
            out.put(toCamel(key), val);
        }
        // createTime 统一以 "yyyy-MM-dd'T'HH:mm:ss" 字符串返回，前端用 substring 即可解析
        if (createTime != null && !createTime.isEmpty() && createTime.length() >= 19) {
            out.put("createTime", createTime.substring(0, 10) + "T" + createTime.substring(11, 19));
        } else {
            out.put("createTime", "");
        }
        return out;
    }

    @GetMapping("/user/{userId}")
    public Result<List<Map<String, Object>>> getByUserId(@PathVariable Long userId) {
        Result<List<Map<String, Object>>> rawResult = messageService.getByUserIdAsMap(userId);
        if (rawResult.getCode() != 200 || rawResult.getData() == null) {
            return Result.success(new ArrayList<>());
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rawResult.getData()) {
            Map<String, Object> map = convertRow(row);
            Object senderIdObj = map.get("senderUserId");
            Object receiverIdObj = map.get("userId");
            Long senderId = (senderIdObj != null && !senderIdObj.toString().isEmpty()) ? Long.valueOf(senderIdObj.toString()) : null;
            Long receiverId = (receiverIdObj != null && !receiverIdObj.toString().isEmpty()) ? Long.valueOf(receiverIdObj.toString()) : null;
            // 系统通知：标题固定为"系统通知"，额外返回双方昵称用于联系对方
            Object typeObj = map.get("type");
            boolean isSystemNotice = typeObj != null && Integer.valueOf(typeObj.toString()) == 1;
            if (isSystemNotice) {
                map.put("senderNickName", "系统通知");
                // 系统通知的对方身份：当前用户是买家时，对方是卖家(senderUserId)，当前用户是卖家时，对方是买家(userId)
                // 这里直接返回对方用户的ID和昵称，由前端自行判断
                // 简化：返回 senderUserId 存储的是"对方用户"，与 receiverUserId 是对方用户ID
                Long counterpartyUserId = null;
                String counterpartyName = null;
                if (userId.equals(senderId)) {
                    // 我是买家 → 对方是卖家（receiverId）
                    counterpartyUserId = receiverId;
                } else {
                    // 我是卖家 → 对方是买家（senderId）
                    counterpartyUserId = senderId;
                }
                if (counterpartyUserId != null) {
                    User counterparty = userService.getById(counterpartyUserId);
                    counterpartyName = counterparty != null ? counterparty.getNickName() : "用户";
                }
                map.put("counterpartyUserId", counterpartyUserId);
                map.put("counterpartyName", counterpartyName);
            } else if (senderId != null) {
                User sender = userService.getById(senderId);
                map.put("senderNickName", sender != null ? sender.getNickName() : "用户");
            } else {
                map.put("senderNickName", "系统通知");
            }
            // 额外返回接收者昵称（非系统通知时使用）
            if (receiverId != null) {
                User receiver = userService.getById(receiverId);
                map.put("receiverNickName", receiver != null ? receiver.getNickName() : "用户");
            }
            result.add(map);
        }
        return Result.success(result);
    }

    @GetMapping("/{messageId}")
    public Result<Map<String, Object>> getById(@PathVariable Long messageId) {
        Result<Map<String, Object>> rawResult = messageService.getByIdAsMap(messageId);
        if (rawResult.getCode() != 200 || rawResult.getData() == null) {
            return Result.fail("消息不存在");
        }
        Map<String, Object> map = convertRow(rawResult.getData());
        Object senderIdObj = map.get("senderUserId");
        Object receiverIdObj = map.get("userId");
        Long senderId = (senderIdObj != null && !senderIdObj.toString().isEmpty()) ? Long.valueOf(senderIdObj.toString()) : null;
        Long receiverId = (receiverIdObj != null && !receiverIdObj.toString().isEmpty()) ? Long.valueOf(receiverIdObj.toString()) : null;
        Object typeObj = map.get("type");
        boolean isSystemNotice = typeObj != null && Integer.valueOf(typeObj.toString()) == 1;
        if (isSystemNotice) {
            map.put("senderNickName", "系统通知");
            // 详情页不需要当前用户信息，直接用 senderUserId 和 userId 让前端判断
            if (senderId != null) {
                User sender = userService.getById(senderId);
                map.put("senderUserName", sender != null ? sender.getNickName() : "用户");
            }
            if (receiverId != null) {
                User receiver = userService.getById(receiverId);
                map.put("receiverUserName", receiver != null ? receiver.getNickName() : "用户");
            }
        } else if (senderId != null) {
            User sender = userService.getById(senderId);
            map.put("senderNickName", sender != null ? sender.getNickName() : "系统用户");
        } else {
            map.put("senderNickName", "系统通知");
        }
        return Result.success(map);
    }

    @PostMapping("/read/{messageId}")
    public Result<Void> markAsRead(@PathVariable Long messageId) {
        return messageService.markAsRead(messageId);
    }

    @PostMapping("/read/all/{userId}")
    public Result<Void> markAllAsRead(@PathVariable Long userId) {
        return messageService.markAllAsRead(userId);
    }

    @PostMapping("/read/conversation/{userId}/{otherUserId}")
    public Result<Void> markConversationAsRead(
            @PathVariable Long userId,
            @PathVariable Long otherUserId) {
        return messageService.markConversationAsRead(userId, otherUserId);
    }

    @GetMapping("/unread/count/{userId}")
    public Result<Integer> getUnreadCount(@PathVariable Long userId) {
        return messageService.getUnreadCount(userId);
    }

    @PostMapping("/send")
    public Result<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> params) {
        Object senderUserIdObj = params.get("senderUserId");
        Long senderUserId = senderUserIdObj != null ? Long.valueOf(String.valueOf(senderUserIdObj)) : null;
        Object userIdObj = params.get("userId");
        Long receiverUserId = userIdObj != null ? Long.valueOf(String.valueOf(userIdObj)) : null;
        Object typeObj = params.get("type");
        Integer type = typeObj != null ? Integer.valueOf(String.valueOf(typeObj)) : 2;
        String title = params.get("title") != null ? String.valueOf(params.get("title")) : null;
        String content = params.get("content") != null ? String.valueOf(params.get("content")) : null;
        String itemType = params.get("itemType") != null ? String.valueOf(params.get("itemType")) : null;
        Object itemIdObj = params.get("itemId");
        Long itemId = itemIdObj != null ? Long.valueOf(String.valueOf(itemIdObj)) : null;
        String itemTitle = params.get("itemTitle") != null ? String.valueOf(params.get("itemTitle")) : null;
        String createTime = params.get("createTime") != null ? String.valueOf(params.get("createTime")) : null;
        return messageService.sendMessageReturnMap(senderUserId, receiverUserId, type, title, content, itemType, itemId, itemTitle, createTime);
    }

    @GetMapping("/conversation/{userId}/{otherUserId}")
    public Result<List<Map<String, Object>>> getConversation(
            @PathVariable Long userId,
            @PathVariable Long otherUserId) {
        Result<List<Map<String, Object>>> rawResult = messageService.getConversationAsMap(userId, otherUserId);
        if (rawResult.getCode() != 200 || rawResult.getData() == null) {
            return Result.success(new ArrayList<>());
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rawResult.getData()) {
            Map<String, Object> map = convertRow(row);
            map.put("isMine", userId.equals(map.get("senderUserId")));
            result.add(map);
        }
        return Result.success(result);
    }
}
