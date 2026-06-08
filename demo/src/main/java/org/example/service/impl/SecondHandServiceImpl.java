package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.Result;
import org.example.entity.SecondHand;
import org.example.mapper.SecondHandMapper;
import org.example.service.SecondHandService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SecondHandServiceImpl extends ServiceImpl<SecondHandMapper, SecondHand> implements SecondHandService {

    @Override
    public Result<String> publish(SecondHand secondHand) {
        if (secondHand.getUserId() == null || !StringUtils.hasText(secondHand.getGoodsName())) {
            return Result.fail("用户ID和物品名称为必填项");
        }
        secondHand.setStatus(0);
        boolean save = this.save(secondHand);
        return save ? Result.success("发布成功，等待管理员审核") : Result.fail("发布失败");
    }


    @Override
    public Result<List<SecondHand>> listByCondition(String goodsType, Double minPrice, Double maxPrice, Integer quality) {
        LambdaQueryWrapper<SecondHand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SecondHand::getStatus, 1);
        if (StringUtils.hasText(goodsType)) {
            wrapper.eq(SecondHand::getGoodsType, goodsType);
        }
        if (minPrice != null) {
            wrapper.ge(SecondHand::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(SecondHand::getPrice, maxPrice);
        }
        if (quality != null) {
            wrapper.eq(SecondHand::getQuality, quality);
        }
        wrapper.orderByDesc(SecondHand::getCreateTime);
        List<SecondHand> list = this.list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<String> updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            return Result.fail("物品ID和状态不能为空");
        }
        SecondHand secondHand = new SecondHand();
        secondHand.setSecondId(id);
        secondHand.setStatus(status);
        boolean update = this.updateById(secondHand);
        return update ? Result.success("状态修改成功") : Result.fail("状态修改失败");
    }

    @Override
    public Result<List<SecondHand>> listByUserId(Long userId) {
        if (userId == null) {
            return Result.fail("用户ID不能为空");
        }
        LambdaQueryWrapper<SecondHand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SecondHand::getUserId, userId);
        wrapper.orderByDesc(SecondHand::getCreateTime);
        List<SecondHand> list = this.list(wrapper);
        return Result.success(list);
    }
}