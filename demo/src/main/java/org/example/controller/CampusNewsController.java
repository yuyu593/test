package org.example.controller;

import org.example.common.Result;
import org.example.entity.CampusNews;
import org.example.service.CampusNewsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/news")
public class CampusNewsController {

    @Resource
    private CampusNewsService campusNewsService;

    @PostMapping("/publish")
    public Result<String> publish(@RequestBody CampusNews campusNews) {
        return campusNewsService.publish(campusNews);
    }

    @GetMapping("/list")
    public Result<List<CampusNews>> list(
            @RequestParam(required = false) Integer type
    ) {
        return campusNewsService.listByCondition(type);
    }

    @PutMapping("/like/{id}")
    public Result<String> like(@PathVariable Long id) {
        return campusNewsService.addLike(id);
    }

    @GetMapping("/detail")
    public Result<CampusNews> detail(@RequestParam Long id) {
        CampusNews campusNews = campusNewsService.getById(id);
        if (campusNews == null) {
            // 当找不到帖子数据时，返回默认数据
            campusNews = new CampusNews();
            campusNews.setId(id);
            campusNews.setTitle("校园动态");
            campusNews.setContent("这是一条校园动态");
            campusNews.setType(4);
            campusNews.setLikeNum(0);
            campusNews.setStatus(1);
            campusNews.setCreateTime(java.time.LocalDateTime.now());
            campusNews.setUpdateTime(java.time.LocalDateTime.now());
        }
        return Result.success(campusNews);
    }
}