package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.Result;
import org.example.entity.User;

public interface UserService extends IService<User> {
    Result<User> login(String studentNo, String password);
    Result<String> register(User user);
    Result<String> updateUserInfo(User user);
    Result<User> getUserInfo(Long userId);
}