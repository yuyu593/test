package org.example.controller;

import org.example.common.Result;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        return userService.login(user.getStudentNo(), user.getPassword());
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        return userService.register(user);
    }

    @PutMapping("/update")
    public Result<String> updateUserInfo(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }

    @GetMapping("/info/{userId}")
    public Result<User> getUserInfo(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }
}