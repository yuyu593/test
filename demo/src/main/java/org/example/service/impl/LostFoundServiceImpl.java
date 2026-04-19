package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.Result;
import org.example.entity.LostFound;
import org.example.mapper.LostFoundMapper;
import org.example.service.LostFoundService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class LostFoundServiceImpl extends ServiceImpl<LostFoundMapper, LostFound> implements LostFoundService {

    @Override
    public Result<String> publish(LostFound lostFound) {
        if (lostFound.getUserId() == null || !StringUtils.hasText(lostFound.getGoodsName())) {
            return Result.fail("用户ID和物品名称为必填项");
        }
        lostFound.setStatus(0);
        boolean save = this.save(lostFound);
        return save ? Result.success("发布成功，等待管理员审核") : Result.fail("发布失败");
    }

    @Override
    public Result<List<LostFound>> listByCondition(Integer type, String address) {
        LambdaQueryWrapper<LostFound> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LostFound::getStatus, 1);
        if (type != null) {
            wrapper.eq(LostFound::getType, type);
        }
        if (StringUtils.hasText(address)) {
            wrapper.like(LostFound::getAddress, address);
        }
        wrapper.orderByDesc(LostFound::getCreateTime);
        List<LostFound> list = this.list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<LostFound> getById(Long id) {
        if (id == null) {
            return Result.fail("信息ID不能为空");
        }
        LostFound lostFound = this.baseMapper.selectById(id);
        return lostFound != null ? Result.success(lostFound) : Result.fail("信息不存在");
    }

    @Override
    public Result<String> updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            return Result.fail("信息ID和状态不能为空");
        }
        LostFound lostFound = new LostFound();
        lostFound.setId(id);
        lostFound.setStatus(status);
        boolean update = this.updateById(lostFound);
        return update ? Result.success("状态修改成功") : Result.fail("状态修改失败");
    }

    @Override
    public Result<List<LostFound>> listByUserId(Long userId) {
        if (userId == null) {
            return Result.fail("用户ID不能为空");
        }
        LambdaQueryWrapper<LostFound> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LostFound::getUserId, userId);
        wrapper.orderByDesc(LostFound::getCreateTime);
        List<LostFound> list = this.list(wrapper);
        return Result.success(list);
    }
}