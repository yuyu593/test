package org.example.controller;

import org.example.common.Result;
import org.example.entity.SecondHand;
import org.example.entity.User;
import org.example.service.SecondHandService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/second")
public class SecondHandController {

    @Resource
    private SecondHandService secondHandService;

    @Resource
    private UserService userService;

    @PostMapping("/publish")
    public Result<String> publish(@RequestBody SecondHand secondHand) {
        return secondHandService.publish(secondHand);
    }

    @GetMapping("/list")
    public Result<List<SecondHand>> list(
            @RequestParam(required = false) String goodsType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer quality
    ) {
        return secondHandService.listByCondition(goodsType, minPrice, maxPrice, quality);
    }

    @PutMapping("/status")
    public Result<String> updateStatus(
            @RequestParam Long id,
            @RequestParam Integer status
    ) {
        return secondHandService.updateStatus(id, status);
    }

    @GetMapping("/user/{userId}")
    public Result<List<SecondHand>> userList(@PathVariable Long userId) {
        return secondHandService.listByUserId(userId);
    }

    @PutMapping("/admin/audit")
    public Result<String> audit(
            @RequestParam Long id,
            @RequestParam Integer status
    ) {
        return secondHandService.updateStatus(id, status);
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        SecondHand s = secondHandService.getById(id);
        if (s == null) {
            return Result.error();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", s.getSecondId());
        map.put("title", s.getGoodsName());
        map.put("price", s.getPrice());
        map.put("img_urls", s.getImgUrls() == null ? "" : s.getImgUrls());
        map.put("create_time", s.getCreateTime());
        map.put("quality", s.getQuality());
        map.put("tradeAddress", s.getTradeAddress());
        map.put("goodsDesc", s.getGoodsDesc());

        // 加载用户信息（头像+昵称）
        if (s.getUserId() != null) {
            User user = userService.getById(s.getUserId());
            if (user != null) {
                map.put("nickname", user.getNickName());
                map.put("avatar", user.getAvatar());
            } else {
                map.put("nickname", "匿名用户");
                map.put("avatar", "");
            }
        } else {
            map.put("nickname", "匿名用户");
            map.put("avatar", "");
        }

        return Result.success(map);
    }
}