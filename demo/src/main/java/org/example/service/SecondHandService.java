package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.Result;
import org.example.entity.SecondHand;

import java.util.List;

public interface SecondHandService extends IService<SecondHand> {
    Result<String> publish(SecondHand secondHand);
    Result<List<SecondHand>> listByCondition(String goodsType, Double minPrice, Double maxPrice, Integer quality);
    Result<String> updateStatus(Long id, Integer status);
    Result<List<SecondHand>> listByUserId(Long userId);
}