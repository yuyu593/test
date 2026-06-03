package org.example.controller;

import org.example.common.Result;
import org.example.service.PublishService;
import org.example.vo.PublishVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/publish")
public class PublishController {

    @Resource
    private PublishService publishService;

    @GetMapping("/my/list")
    public Result<List<PublishVO>> getMyPublish(@RequestParam Long userId){
        List<PublishVO> list = publishService.getByUserId(userId);
        return Result.success(list);
    }

    @GetMapping("/count")
    public Result<Integer> count(@RequestParam Long userId){
        List<PublishVO> all = publishService.getByUserId(userId);
        return Result.success(all.size());
    }
}