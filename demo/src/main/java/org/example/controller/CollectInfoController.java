package org.example.controller;

import org.example.common.Result;
import org.example.dto.CollectDto;
import org.example.service.CollectInfoService;
import org.example.vo.CollectVo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/collect")
public class CollectInfoController {

    @Resource
    private CollectInfoService collectService;

    // 收藏/取消收藏（统一接口）
    @PostMapping("/toggle")
    public Result<String> toggleCollect(@RequestBody CollectDto dto) {
        return collectService.toggleCollect(dto.getUserId(), dto.getTargetId(), dto.getTargetType());
    }

    // 查询用户的收藏列表
    @GetMapping("/list/{userId}")
    public Result<List<CollectVo>> list(@PathVariable Long userId) {
        return Result.success(collectService.listUserCollects(userId));
    }

    // 查询某条内容的收藏数
    @GetMapping("/count")
    public Result<Integer> count(
            @RequestParam Long targetId,
            @RequestParam Integer targetType) {
        return Result.success(collectService.countTargetCollects(targetId, targetType));
    }
}