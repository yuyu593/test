package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.Result;
import org.example.entity.PurchaseInfo;
import org.example.mapper.PurchaseInfoMapper;
import org.example.service.PurchaseInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PurchaseInfoServiceImpl extends ServiceImpl<PurchaseInfoMapper, PurchaseInfo> implements PurchaseInfoService {

    @Override
    public Result<String> publish(PurchaseInfo purchaseInfo) {
        if (purchaseInfo.getUserId() == null || !StringUtils.hasText(purchaseInfo.getTitle())) {
            return Result.fail("用户ID和求购标题为必填项");
        }
        // 发布默认状态为 0（待审核/有效，按你的业务调整）
        purchaseInfo.setStatus(0);
        boolean save = this.save(purchaseInfo);
        return save ? Result.success("发布成功，等待管理员审核") : Result.fail("发布失败");
    }

    @Override
    public Result<List<PurchaseInfo>> listByCondition(String category, Double minPrice, Double maxPrice) {
        LambdaQueryWrapper<PurchaseInfo> wrapper = new LambdaQueryWrapper<>();
        // 只查有效、未删除的
        wrapper.eq(PurchaseInfo::getStatus, 0);
        if (StringUtils.hasText(category)) {
            wrapper.eq(PurchaseInfo::getCategory, category);
        }
        if (minPrice != null) {
            wrapper.ge(PurchaseInfo::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(PurchaseInfo::getPrice, maxPrice);
        }
        wrapper.orderByDesc(PurchaseInfo::getCreateTime);
        List<PurchaseInfo> list = this.list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<String> updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            return Result.fail("求购ID和状态不能为空");
        }
        PurchaseInfo purchaseInfo = new PurchaseInfo();
        purchaseInfo.setId(id);
        purchaseInfo.setStatus(status);
        boolean update = this.updateById(purchaseInfo);
        return update ? Result.success("状态修改成功") : Result.fail("状态修改失败");
    }

    @Override
    public Result<List<PurchaseInfo>> listByUserId(Long userId) {
        if (userId == null) {
            return Result.fail("用户ID不能为空");
        }
        LambdaQueryWrapper<PurchaseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseInfo::getUserId, userId);
        wrapper.orderByDesc(PurchaseInfo::getCreateTime);
        List<PurchaseInfo> list = this.list(wrapper);
        return Result.success(list);
    }
}