package org.example.controller;

import org.example.common.Result;
import org.example.entity.PurchaseRecord;
import org.example.service.PurchaseRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/purchaseRecord")
public class PurchaseRecordController {

    @Resource
    private PurchaseRecordService purchaseRecordService;

    @PostMapping("/create")
    public Result<Long> createPurchase(@RequestBody PurchaseRecord record) {
        return purchaseRecordService.createPurchase(record.getSecondId(), record.getUserId());
    }

    @PostMapping("/pay/{recordId}")
    public Result<String> pay(@PathVariable Long recordId) {
        return purchaseRecordService.pay(recordId);
    }

    @GetMapping("/user/{userId}")
    public Result<List<PurchaseRecord>> listByUserId(@PathVariable Long userId) {
        return purchaseRecordService.listByUserId(userId);
    }

    @GetMapping("/{recordId}")
    public Result<PurchaseRecord> getById(@PathVariable Long recordId) {
        return purchaseRecordService.findById(recordId);
    }
}