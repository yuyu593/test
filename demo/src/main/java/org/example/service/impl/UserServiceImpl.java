package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.Result;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Result<User> login(String studentNo, String password) {
        if (!StringUtils.hasText(studentNo) || !StringUtils.hasText(password)) {
            return Result.fail("学号或密码不能为空");
        }
        User user = baseMapper.selectByStudentNo(studentNo);
        if (user == null) {
            return Result.fail("该学号未注册");
        }
        if (!user.getPassword().equals(password)) {
            return Result.fail("密码错误");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<String> register(User user) {
        if (baseMapper.selectByStudentNo(user.getStudentNo()) != null) {
            return Result.fail("该学号已注册");
        }
        if (!StringUtils.hasText(user.getStudentNo()) || !StringUtils.hasText(user.getPassword())) {
            return Result.fail("学号和密码为必填项");
        }
        boolean save = this.save(user);
        return save ? Result.success("注册成功") : Result.fail("注册失败，请重试");
    }

    @Override
    public Result<String> updateUserInfo(User user) {
        if (user.getUserId() == null) {
            return Result.fail("用户ID不能为空");
        }
        boolean update = this.updateById(user);
        return update ? Result.success("修改成功") : Result.fail("修改失败");
    }

    @Override
    public Result<User> getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

}