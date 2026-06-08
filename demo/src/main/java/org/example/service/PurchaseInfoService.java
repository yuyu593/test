package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.Result;
import org.example.entity.PurchaseInfo;
import java.util.List;

public interface PurchaseInfoService extends IService<PurchaseInfo> {
    Result<String> publish(PurchaseInfo purchaseInfo);
    Result<List<PurchaseInfo>> listByCondition(String category, Double minPrice, Double maxPrice);
    Result<String> updateStatus(Long id, Integer status);
    Result<List<PurchaseInfo>> listByUserId(Long userId);
}