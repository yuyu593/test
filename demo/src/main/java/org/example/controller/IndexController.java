package org.example.controller;

import org.example.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public Result<String> index() {
        return Result.success("校园服务后端运行成功！");
    }
}