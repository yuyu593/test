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
}