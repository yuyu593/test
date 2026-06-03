package org.example.controller;

import org.example.common.Result;
import org.example.entity.LostFound;
import org.example.entity.User;
import org.example.service.LostFoundService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lost")
public class LostFoundController {

    @Resource
    private LostFoundService lostFoundService;

    @Resource
    private UserService userService;

    // 发布
    @PostMapping("/publish")
    public Result<String> publish(@RequestBody LostFound lostFound) {
        return lostFoundService.publish(lostFound);
    }

    // 列表（支持类型+地址筛选）
    @GetMapping("/list")
    public Result<List<LostFound>> list(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String address
    ) {
        return lostFoundService.listByCondition(type, address);
    }

    // 修改状态
    @PutMapping("/status")
    public Result<String> updateStatus(
            @RequestParam Long id,
            @RequestParam Integer status
    ) {
        return lostFoundService.updateStatus(id, status);
    }

    // 根据用户ID查询
    @GetMapping("/user/{userId}")
    public Result<List<LostFound>> userList(@PathVariable Long userId) {
        return lostFoundService.listByUserId(userId);
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        LostFound lost = lostFoundService.getById(id);
        if (lost == null) {
            return Result.error();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", lost.getLostId());
        map.put("goodsName", lost.getGoodsName());
        map.put("goodsDesc", lost.getGoodsDesc());
        map.put("img_urls", lost.getImgUrls() == null ? "" : lost.getImgUrls());
        map.put("address", lost.getAddress());
        map.put("type", lost.getType());
        map.put("create_time", lost.getCreateTime());
        map.put("happenTime", lost.getHappenTime());

        // 用户信息
        if (lost.getUserId() != null) {
            User user = userService.getById(lost.getUserId());
            if (user != null) {
                map.put("nickname", user.getNickName());
                map.put("avatar", user.getAvatar());
            } else {
                map.put("nickname", "匿名用户");
                map.put("avatar", "default.png");
            }
        } else {
            map.put("nickname", "匿名用户");
            map.put("avatar", "default.png");
        }

        return Result.success(map);
    }
}