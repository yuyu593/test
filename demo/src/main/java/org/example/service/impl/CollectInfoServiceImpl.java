package org.example.service.impl;

import org.example.common.Result;
import org.example.entity.CollectInfo;
import org.example.mapper.CollectInfoMapper;
import org.example.service.CollectInfoService;
import org.example.vo.CollectVo;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectInfoServiceImpl implements CollectInfoService {

    @Resource
    private CollectInfoMapper collectMapper;

    @Override
    public Result<String> toggleCollect(Long userId, Long targetId, Integer targetType) {
        int count = collectMapper.countExists(userId, targetId, targetType);
        if (count > 0) {
            collectMapper.delete(userId, targetId, targetType);
            return Result.success("取消收藏成功");
        } else {
            CollectInfo collect = new CollectInfo();
            collect.setUserId(userId);
            collect.setTargetId(targetId);
            collect.setTargetType(targetType);
            collectMapper.insert(collect);
            return Result.success("收藏成功");
        }
    }

    @Override
    public List<CollectVo> listUserCollects(Long userId) {
        List<CollectVo> resultList = new ArrayList<>();
        // 依次查询四类收藏，合并集合
        resultList.addAll(collectMapper.listCollectSecond(userId));
        resultList.addAll(collectMapper.listCollectPurchase(userId));
        resultList.addAll(collectMapper.listCollectNews(userId));
        resultList.addAll(collectMapper.listCollectLost(userId));
        return resultList;
    }

    @Override
    public int countTargetCollects(Long targetId, Integer targetType) {
        return collectMapper.countByTarget(targetId, targetType);
    }
}