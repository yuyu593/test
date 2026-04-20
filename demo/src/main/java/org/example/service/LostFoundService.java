package org.example.service;

import org.example.common.Result;
import org.example.entity.LostFound;

import java.util.List;

public interface LostFoundService {

    Result<String> publish(LostFound lostFound);

    Result<List<LostFound>> listByCondition(Integer type, String address);

    Result<LostFound> getById(Long id);

    Result<String> updateStatus(Long id, Integer status);

    Result<List<LostFound>> listByUserId(Long userId);
}