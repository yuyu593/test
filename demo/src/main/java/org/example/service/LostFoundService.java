package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.Result;
import org.example.entity.LostFound;

import java.util.List;

public interface LostFoundService extends IService<LostFound> {
    Result<String> publish(LostFound lostFound);
    Result<List<LostFound>> listByCondition(Integer type, String address);
    Result<String> updateStatus(Long id, Integer status);
    Result<List<LostFound>> listByUserId(Long userId);
}