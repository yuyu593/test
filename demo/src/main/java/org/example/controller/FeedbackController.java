package org.example.controller;

import org.example.entity.Feedback;
import org.example.service.FeedbackService;
import org.example.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;

    // 你这段提交反馈的接口就放在这里
    @PostMapping("/submit")
    public Result submitFeedback(@RequestBody Feedback feedback, 
                                 @RequestHeader("userId") Long userId) {
        feedback.setUserId(userId);
        feedback.setStatus(0); // 0:待处理
        feedbackService.save(feedback);
        return Result.success();
    }
}