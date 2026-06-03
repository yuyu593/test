package org.example.controller;

import org.example.common.Result;
import org.example.entity.PurchaseInfo;
import org.example.entity.User;
import org.example.service.PurchaseInfoService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/purchase")
public class PurchaseInfoController {

    @Resource
    private PurchaseInfoService purchaseInfoService;

    @Resource
    private UserService userService; // 注入用户，用于拿头像昵称

    /**
     * 发布求购信息
     */
    @PostMapping("/publish")
    public Result<String> publish(@RequestBody PurchaseInfo purchaseInfo) {
        return purchaseInfoService.publish(purchaseInfo);
    }

    /**
     * 条件查询求购列表（分类、价格区间）
     */
    @GetMapping("/list")
    public Result<List<PurchaseInfo>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        return purchaseInfoService.listByCondition(category, minPrice, maxPrice);
    }

    /**
     * 修改求购状态（上架/下架）
     */
    @PutMapping("/status")
    public Result<String> updateStatus(
            @RequestParam Long id,
            @RequestParam Integer status
    ) {
        return purchaseInfoService.updateStatus(id, status);
    }

    /**
     * 查询用户自己发布的求购
     */
    @GetMapping("/user/{userId}")
    public Result<List<PurchaseInfo>> userList(@PathVariable Long userId) {
        return purchaseInfoService.listByUserId(userId);
    }

    @GetMapping("/detail")
    public Result<Map<String, Object>> detail(@RequestParam("id") Long id) {
        PurchaseInfo info = purchaseInfoService.getById(id);
        if (info == null) {
            return Result.error();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", info.getPurchaseId());
        map.put("title", info.getTitle());
        map.put("content", info.getContent());
        map.put("category", info.getCategory());
        map.put("price", info.getPrice());
        map.put("contact", info.getContact());
        map.put("isUrgent", info.getIsUrgent());
        map.put("createTime", info.getCreateTime());

        // 加载用户头像+昵称
        if (info.getUserId() != null) {
            User user = userService.getById(info.getUserId());
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