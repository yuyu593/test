package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.Result;
import org.example.entity.CampusNews;
import org.example.mapper.CampusNewsMapper;
import org.example.service.CampusNewsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampusNewsServiceImpl extends ServiceImpl<CampusNewsMapper, CampusNews> implements CampusNewsService {

    @Override
    public Result<String> publish(CampusNews campusNews) {
        if (campusNews.getPublishId() == null || !org.springframework.util.StringUtils.hasText(campusNews.getTitle())) {
            return Result.fail("发布者ID和标题为必填项");
        }
        campusNews.setStatus(0);
        boolean save = this.save(campusNews);
        return save ? Result.success("发布成功，等待管理员审核") : Result.fail("发布失败");
    }

    @Override
    public Result<List<CampusNews>> listByCondition(Integer type) {
        LambdaQueryWrapper<CampusNews> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CampusNews::getStatus, 1);
        if (type != null) {
            wrapper.eq(CampusNews::getType, type);
        }
        wrapper.orderByDesc(CampusNews::getCreateTime);
        List<CampusNews> list = this.list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<String> updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            return Result.fail("资讯ID和状态不能为空");
        }
        CampusNews campusNews = new CampusNews();
        campusNews.setId(id);
        campusNews.setStatus(status);
        boolean update = this.updateById(campusNews);
        return update ? Result.success("状态修改成功") : Result.fail("状态修改失败");
    }

    @Override
    public Result<String> addLike(Long id) {
        if (id == null) {
            return Result.fail("资讯ID不能为空");
        }
        CampusNews campusNews = this.getById(id);
        if (campusNews == null) {
            return Result.fail("资讯不存在");
        }
        campusNews.setLikeNum(campusNews.getLikeNum() + 1);
        boolean update = this.updateById(campusNews);
        return update ? Result.success("点赞成功") : Result.fail("点赞失败");
    }

    @Override
    public Result<List<CampusNews>> listByPublishId(Long publishId) {
        if (publishId == null) {
            return Result.fail("发布者ID不能为空");
        }
        LambdaQueryWrapper<CampusNews> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CampusNews::getPublishId, publishId);
        wrapper.orderByDesc(CampusNews::getCreateTime);
        List<CampusNews> list = this.list(wrapper);
        return Result.success(list);
    }
}