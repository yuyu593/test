package org.example.controller;

import org.example.common.Result;
import org.example.entity.PurchaseInfo;
import org.example.service.PurchaseInfoService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/purchase")
public class PurchaseInfoController {

    @Resource
    private PurchaseInfoService purchaseInfoService;

    /**
     * 发布求购信息
     */
    @PostMapping("/publish")
    public Result<String> publish(@RequestBody PurchaseInfo purchaseInfo) {
        return purchaseInfoService.publish(purchaseInfo);
    }

    /**
     * 条件查询求购列表（分类、价格区间）
     */
    @GetMapping("/list")
    public Result<List<PurchaseInfo>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        return purchaseInfoService.listByCondition(category, minPrice, maxPrice);
    }

    /**
     * 修改求购状态（上架/下架）
     */
    @PutMapping("/status")
    public Result<String> updateStatus(
            @RequestParam Long id,
            @RequestParam Integer status
    ) {
        return purchaseInfoService.updateStatus(id, status);
    }

    /**
     * 查询用户自己发布的求购
     */
    @GetMapping("/user/{userId}")
    public Result<List<PurchaseInfo>> userList(@PathVariable Long userId) {
        return purchaseInfoService.listByUserId(userId);
    }
}