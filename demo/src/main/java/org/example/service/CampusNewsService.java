package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.Result;
import org.example.entity.CampusNews;

import java.util.List;

public interface CampusNewsService extends IService<CampusNews> {
    Result<String> publish(CampusNews campusNews);
    Result<List<CampusNews>> listByCondition(Integer type);
    Result<String> updateStatus(Long id, Integer status);
    Result<String> addLike(Long id);
    Result<List<CampusNews>> listByPublishId(Long publishId);
}