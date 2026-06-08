package org.example.service.impl;

import org.example.mapper.PublishMapper;
import org.example.service.PublishService;
import org.example.vo.PublishVO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class PublishServiceImpl implements PublishService {

    @Resource
    private PublishMapper publishMapper;

    @Override
    public List<PublishVO> getByUserId(Long userId) {
        return publishMapper.selectAllByUserId(userId);
    }
}