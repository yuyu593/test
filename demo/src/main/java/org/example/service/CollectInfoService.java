package org.example.service;

import org.example.common.Result;
import org.example.entity.CollectInfo;
import org.example.vo.CollectVo;
import java.util.List;

public interface CollectInfoService {
    Result<String> toggleCollect(Long userId, Long targetId, Integer targetType);

    // 返回 VO 集合（带标题、内容）
    List<CollectVo> listUserCollects(Long userId);

    int countTargetCollects(Long targetId, Integer targetType);
}