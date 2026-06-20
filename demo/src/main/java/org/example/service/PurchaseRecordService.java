package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.Result;
import org.example.entity.PurchaseRecord;

import java.util.List;

public interface PurchaseRecordService extends IService<PurchaseRecord> {
    Result<Long> createPurchase(Long secondId, Long userId);
    Result<String> pay(Long recordId);
    Result<List<PurchaseRecord>> listByUserId(Long userId);
    Result<PurchaseRecord> findById(Long recordId);
}