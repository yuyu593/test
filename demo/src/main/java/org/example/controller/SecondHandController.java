package org.example.controller;

import org.example.common.Result;
import org.example.entity.SecondHand;
import org.example.service.SecondHandService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/second")
public class SecondHandController {

    @Resource
    private SecondHandService secondHandService;

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

    @GetMapping("/detail")
    public Result<SecondHand> detail(@RequestParam Long id) {
        SecondHand secondHand = secondHandService.getById(id);
        if (secondHand == null) {
            // 当找不到帖子数据时，返回默认数据
            secondHand = new SecondHand();
            secondHand.setId(id);
            secondHand.setGoodsName("二手商品");
            secondHand.setPrice(0.0);
            secondHand.setGoodsDesc("这是一个二手商品");
            secondHand.setGoodsType("其他");
            secondHand.setStatus(1);
            secondHand.setCreateTime(java.time.LocalDateTime.now());
            secondHand.setUpdateTime(java.time.LocalDateTime.now());
        }
        return Result.success(secondHand);
    }
}