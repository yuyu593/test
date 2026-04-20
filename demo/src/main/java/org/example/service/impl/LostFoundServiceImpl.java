package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.Result;
import org.example.entity.LostFound;
import org.example.mapper.LostFoundMapper;
import org.example.service.LostFoundService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

@Service
public class LostFoundServiceImpl extends ServiceImpl<LostFoundMapper, LostFound> implements LostFoundService {

    // 时间正则：匹配 今天/昨天/前天/xx月xx日/xxxx-xx-xx
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "(今天|昨天|前天|\\d{4}[-/]\\d{1,2}[-/]\\d{1,2}|\\d{1,2}月\\d{1,2}日)"
    );

    @Override
    public Result<String> publish(LostFound lostFound) {
        if (lostFound.getUserId() == null || !StringUtils.hasText(lostFound.getGoodsName())) {
            return Result.fail("用户ID和物品名称为必填项");
        }

        String desc = lostFound.getGoodsDesc();
        if (StringUtils.hasText(desc)) {
            Matcher matcher = DATE_PATTERN.matcher(desc);
            if (matcher.find()) {
                String timeStr = matcher.group(1);
                try {
                    LocalDateTime happenTime = parseTime(timeStr);
                    lostFound.setHappenTime(happenTime);
                } catch (Exception e) {
                    lostFound.setHappenTime(LocalDateTime.now());
                }
            } else {
                lostFound.setHappenTime(LocalDateTime.now());
            }
        } else {
            lostFound.setHappenTime(LocalDateTime.now());
        }

        lostFound.setStatus(0);
        boolean save = this.save(lostFound);
        return save ? Result.success("发布成功，等待审核") : Result.fail("发布失败");
    }

    private LocalDateTime parseTime(String timeStr) {
        LocalDate now = LocalDate.now();
        if ("今天".equals(timeStr)) return LocalDateTime.now();
        if ("昨天".equals(timeStr)) return LocalDateTime.now().minusDays(1);
        if ("前天".equals(timeStr)) return LocalDateTime.now().minusDays(2);

        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter df3 = DateTimeFormatter.ofPattern("M月d日");

        try {
            if (timeStr.contains("-")) return LocalDate.parse(timeStr, df1).atStartOfDay();
            if (timeStr.contains("/")) return LocalDate.parse(timeStr, df2).atStartOfDay();
            if (timeStr.contains("月")) {
                LocalDate date = LocalDate.parse(now.getYear() + "年" + timeStr, DateTimeFormatter.ofPattern("yyyy年M月d日"));
                return date.atStartOfDay();
            }
        } catch (Exception e) {}
        return LocalDateTime.now();
    }

    // ===== 以下代码完全不变，直接保留你原来的 =====
    @Override
    public Result<List<LostFound>> listByCondition(Integer type, String address) {
        LambdaQueryWrapper<LostFound> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LostFound::getStatus, 1);
        if (type != null) wrapper.eq(LostFound::getType, type);
        if (StringUtils.hasText(address)) wrapper.like(LostFound::getAddress, address);
        wrapper.orderByDesc(LostFound::getCreateTime);
        return Result.success(this.list(wrapper));
    }

    @Override
    public Result<LostFound> getById(Long id) {
        if (id == null) {
            return Result.fail("信息ID不能为空");
        }
        LostFound lostFound = this.baseMapper.selectById(id);
        return lostFound != null ? Result.success(lostFound) : Result.fail("信息不存在");
    }

    @Override
    public Result<String> updateStatus(Long id, Integer status) {
        if (id == null || status == null) return Result.fail("参数不能为空");
        LostFound lf = new LostFound();
        lf.setId(id);
        lf.setStatus(status);
        return updateById(lf) ? Result.success("修改成功") : Result.fail("修改失败");
    }

    @Override
    public Result<List<LostFound>> listByUserId(Long userId) {
        if (userId == null) return Result.fail("用户ID不能为空");
        LambdaQueryWrapper<LostFound> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LostFound::getUserId, userId);
        wrapper.orderByDesc(LostFound::getCreateTime);
        return Result.success(this.list(wrapper));
    }
}