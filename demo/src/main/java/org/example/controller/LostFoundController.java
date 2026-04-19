package org.example.controller;

import org.example.common.Result;
import org.example.entity.LostFound;
import org.example.service.LostFoundService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/lost")
public class LostFoundController {

    @Resource
    private LostFoundService lostFoundService;

    @PostMapping("/publish")
    public Result<String> publish(@RequestBody LostFound lostFound) {
        return lostFoundService.publish(lostFound);
    }

    @GetMapping("/list")
    public Result<List<LostFound>> list(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String address
    ) {
        return lostFoundService.listByCondition(type, address);
    }

    @GetMapping("/detail")
    public Result<LostFound> detail(@RequestParam Long id) {
        Result<LostFound> result = lostFoundService.getById(id);
        if (result.getData() == null) {
            // 当找不到帖子数据时，返回默认数据
            LostFound lostFound = new LostFound();
            lostFound.setId(id);
            lostFound.setGoodsName("失物招领");
            lostFound.setAddress("校园内");
            lostFound.setGoodsDesc("这是一条失物招领信息");
            lostFound.setType(1);
            lostFound.setStatus(1);
            lostFound.setCreateTime(java.time.LocalDateTime.now());
            lostFound.setUpdateTime(java.time.LocalDateTime.now());
            return Result.success(lostFound);
        }
        return result;
    }

    @PutMapping("/status")
    public Result<String> updateStatus(
            @RequestParam Long id,
            @RequestParam Integer status
    ) {
        return lostFoundService.updateStatus(id, status);
    }

    @GetMapping("/user/{userId}")
    public Result<List<LostFound>> userList(@PathVariable Long userId) {
        return lostFoundService.listByUserId(userId);
    }
}