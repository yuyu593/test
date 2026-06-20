package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.Result;
import org.example.entity.Message;
import org.example.entity.PurchaseRecord;
import org.example.entity.SecondHand;
import org.example.entity.User;
import org.example.mapper.PurchaseRecordMapper;
import org.example.service.MessageService;
import org.example.service.PurchaseRecordService;
import org.example.service.SecondHandService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class PurchaseRecordServiceImpl extends ServiceImpl<PurchaseRecordMapper, PurchaseRecord> implements PurchaseRecordService {

    @Resource
    private SecondHandService secondHandService;

    @Resource
    private MessageService messageService;

    @Resource
    private UserService userService;

    @Override
    @Transactional
    public Result<Long> createPurchase(Long secondId, Long userId) {
        if (secondId == null || userId == null) {
            return Result.fail("商品ID和用户ID不能为空");
        }
        SecondHand secondHand = secondHandService.getById(secondId);
        if (secondHand == null) {
            return Result.fail("商品不存在");
        }
        if (secondHand.getStatus() != 1) {
            return Result.fail("商品已下架或已交易");
        }
        if (secondHand.getUserId().equals(userId)) {
            return Result.fail("不能购买自己发布的商品");
        }

        PurchaseRecord record = new PurchaseRecord();
        record.setUserId(userId);
        record.setSellerId(secondHand.getUserId());
        record.setSecondId(secondId);
        record.setGoodsName(secondHand.getGoodsName());
        record.setGoodsType(secondHand.getGoodsType());
        record.setPrice(secondHand.getPrice());
        record.setImgUrls(secondHand.getImgUrls());
        record.setStatus(0);

        boolean save = this.save(record);
        if (!save) {
            return Result.fail("创建订单失败");
        }

        secondHand.setStatus(2);
        secondHandService.updateById(secondHand);

        return Result.success(record.getRecordId());
    }

    @Override
    @Transactional
    public Result<String> pay(Long recordId) {
        if (recordId == null) {
            return Result.fail("订单ID不能为空");
        }
        PurchaseRecord record = this.getById(recordId);
        if (record == null) {
            return Result.fail("订单不存在");
        }
        if (record.getStatus() != 0) {
            return Result.fail("订单状态异常");
        }

        record.setStatus(1);
        record.setPayTime(LocalDateTime.now());
        boolean update = this.updateById(record);
        if (!update) {
            return Result.fail("付款失败");
        }

        // 付款成功后，给卖家发送一条通知消息（系统通知）
        try {
            User buyer = userService.getById(record.getUserId());
            String buyerName = buyer != null ? buyer.getNickName() : "某用户";
            Message notifySeller = new Message();
            notifySeller.setUserId(record.getSellerId());
            notifySeller.setSenderUserId(record.getUserId());
            notifySeller.setItemType("secondhand");
            notifySeller.setItemId(record.getSecondId());
            notifySeller.setItemTitle(record.getGoodsName());
            notifySeller.setTitle("订单付款通知");
            notifySeller.setContent("买家「" + buyerName + "」已支付「" + record.getGoodsName() + "」订单 ¥" + record.getPrice() + "，请及时处理并发货。");
            notifySeller.setType(1);
            notifySeller.setIsRead(0);
            notifySeller.setCreateTime(new Date());
            messageService.save(notifySeller);

            // 也给买家发一条系统通知
            User seller = userService.getById(record.getSellerId());
            String sellerName = seller != null ? seller.getNickName() : "卖家";
            Message notifyBuyer = new Message();
            notifyBuyer.setUserId(record.getUserId());
            notifyBuyer.setSenderUserId(record.getSellerId());
            notifyBuyer.setItemType("secondhand");
            notifyBuyer.setItemId(record.getSecondId());
            notifyBuyer.setItemTitle(record.getGoodsName());
            notifyBuyer.setTitle("付款成功通知");
            notifyBuyer.setContent("您已成功支付「" + record.getGoodsName() + "」订单 ¥" + record.getPrice() + "，请等待卖家发货，可直接联系卖家" + sellerName + "确认交易细节。");
            notifyBuyer.setType(1);
            notifyBuyer.setIsRead(0);
            notifyBuyer.setCreateTime(new Date());
            messageService.save(notifyBuyer);
        } catch (Exception e) {
            // 消息发送失败不影响订单成功
            System.out.println("发送订单通知消息失败: " + e.getMessage());
        }

        return Result.success("付款成功");
    }

    @Override
    public Result<List<PurchaseRecord>> listByUserId(Long userId) {
        if (userId == null) {
            return Result.fail("用户ID不能为空");
        }
        LambdaQueryWrapper<PurchaseRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseRecord::getUserId, userId);
        wrapper.orderByDesc(PurchaseRecord::getCreateTime);
        List<PurchaseRecord> list = this.list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<PurchaseRecord> findById(Long recordId) {
        PurchaseRecord record = super.getById(recordId);
        return record != null ? Result.success(record) : Result.fail("订单不存在");
    }
}