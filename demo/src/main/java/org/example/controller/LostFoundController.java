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