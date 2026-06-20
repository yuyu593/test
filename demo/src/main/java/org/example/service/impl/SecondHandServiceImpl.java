package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.Result;
import org.example.entity.Message;
import org.example.entity.SecondHand;
import org.example.entity.User;
import org.example.mapper.SecondHandMapper;
import org.example.service.MessageService;
import org.example.service.SecondHandService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SecondHandServiceImpl extends ServiceImpl<SecondHandMapper, SecondHand> implements SecondHandService {

    @Resource
    private MessageService messageService;

    @Resource
    private UserService userService;

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

    @Override
    public Result<List<SecondHand>> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Result.fail("关键词不能为空");
        }
        LambdaQueryWrapper<SecondHand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SecondHand::getStatus, 1);
        wrapper.and(w -> w.like(SecondHand::getGoodsName, keyword)
                .or().like(SecondHand::getGoodsDesc, keyword));
        wrapper.orderByDesc(SecondHand::getCreateTime);
        List<SecondHand> list = this.list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<String> buy(Long secondId, Long userId) {
        if (secondId == null || userId == null) {
            return Result.fail("商品ID和用户ID不能为空");
        }
        SecondHand secondHand = this.getById(secondId);
        if (secondHand == null) {
            return Result.fail("商品不存在");
        }
        if (secondHand.getStatus() != 1) {
            return Result.fail("商品已下架或已交易");
        }
        if (secondHand.getUserId().equals(userId)) {
            return Result.fail("不能购买自己发布的商品");
        }
        // 更新商品状态为已交易
        secondHand.setStatus(2);
        boolean update = this.updateById(secondHand);
        if (update) {
            // 给卖家发送消息通知
            User buyer = userService.getById(userId);
            String buyerName = buyer != null ? buyer.getNickName() : "买家";
            Message message = new Message();
            message.setUserId(secondHand.getUserId());
            message.setSenderUserId(userId);
            message.setItemType("secondhand");
            message.setItemId(secondId);
            message.setItemTitle(secondHand.getGoodsName());
            message.setType(1);
            message.setTitle("商品已售出");
            message.setContent("您发布的闲置商品「" + secondHand.getGoodsName() + "」已被买家「" + buyerName + "」拍下，可直接联系买家确认交易细节~");
            message.setIsRead(0);
            message.setCreateTime(new Date());
            messageService.save(message);
            return Result.success("购买成功");
        }
        return Result.fail("购买失败");
    }
}