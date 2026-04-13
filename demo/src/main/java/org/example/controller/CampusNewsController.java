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
}