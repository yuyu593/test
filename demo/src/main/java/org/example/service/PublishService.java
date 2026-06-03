package org.example.service;
import org.example.vo.PublishVO;
import java.util.List;
public interface PublishService {
    List<PublishVO> getByUserId(Long userId);
}