package org.example.controller;

import org.example.common.Result;
import org.example.entity.CampusNews;
import org.example.entity.User;
import org.example.service.CampusNewsService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/news")
public class CampusNewsController {

    @Resource
    private CampusNewsService campusNewsService;

    @Resource
    private UserService userService; // 注入用户

    @PostMapping("/publish")
    public Result<String> publish(@RequestBody CampusNews campusNews) {
        return campusNewsService.publish(campusNews);
    }

    @GetMapping("/list")
    public Result<java.util.List<CampusNews>> list(
            @RequestParam(required = false) Integer type
    ) {
        return campusNewsService.listByCondition(type);
    }

    @GetMapping("/detail")
    public Result<Map<String, Object>> detail(@RequestParam("id") Long id) {
        CampusNews news = campusNewsService.getById(id);
        if (news == null) {
            return Result.error();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", news.getNewsId());
        map.put("title", news.getTitle());
        map.put("content", news.getContent());
        map.put("img_urls", news.getImgUrls() == null ? "" : news.getImgUrls());
        map.put("createTime", news.getCreateTime());
        map.put("type", news.getType());

        // 加载用户信息（昵称+头像）
        if (news.getPublishId() != null) {
            User user = userService.getById(news.getPublishId());
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

    @PutMapping("/like/{id}")
    public Result<String> like(@PathVariable Long id) {
        return campusNewsService.addLike(id);
    }
}