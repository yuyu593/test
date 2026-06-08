package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.common.Result;
import org.example.entity.CampusNews;
import org.example.entity.LostFound;
import org.example.entity.PurchaseInfo;
import org.example.entity.SecondHand;
import org.example.entity.User;
import org.example.service.CampusNewsService;
import org.example.service.LostFoundService;
import org.example.service.PurchaseInfoService;
import org.example.service.SecondHandService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/square")
public class SquareController {

    @Resource
    private CampusNewsService campusNewsService;

    @Resource
    private PurchaseInfoService purchaseInfoService;

    @Resource
    private LostFoundService lostFoundService;

    @Resource
    private SecondHandService secondHandService;

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getSquareList(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        List<Map<String, Object>> result = new ArrayList<>();

        if (type == null || type.equals("") || type.equals("all")) {
            // 1. 校园动态
            List<CampusNews> newsList = campusNewsService.list(new LambdaQueryWrapper<CampusNews>()
                    .eq(CampusNews::getIsDeleted, 0)
                    .orderByDesc(CampusNews::getCreateTime)
                    .last("LIMIT 100"));

            for (CampusNews news : newsList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", news.getNewsId());
                map.put("type", "news");
                map.put("typeTag", "动态");
                map.put("userId", news.getPublishId());

                if (news.getPublishId() != null) {
                    User user = userService.getById(news.getPublishId());
                    if (user != null) {
                        map.put("nickname", user.getNickName());
                        map.put("avatar", user.getAvatar());
                    } else {
                        map.put("nickname", "匿名用户");
                        map.put("avatar", "");
                    }
                } else {
                    map.put("nickname", "匿名用户");
                    map.put("avatar", "");
                }

                map.put("title", news.getTitle());
                map.put("content", news.getContent());
                map.put("img_urls", news.getImgUrls() == null ? "" : news.getImgUrls());
                map.put("create_time", news.getCreateTime());
                map.put("extraInfo", null);
                result.add(map);
            }

            // 2. 求购信息
            List<PurchaseInfo> purchaseList = purchaseInfoService.list(new LambdaQueryWrapper<PurchaseInfo>()
                    .eq(PurchaseInfo::getIsDeleted, 0)
                    .orderByDesc(PurchaseInfo::getCreateTime)
                    .last("LIMIT 100"));

            for (PurchaseInfo p : purchaseList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getPurchaseId());
                map.put("type", "purchase");
                map.put("typeTag", "求购中");
                map.put("userId", p.getUserId());

                if (p.getUserId() != null) {
                    User user = userService.getById(p.getUserId());
                    if (user != null) {
                        map.put("nickname", user.getNickName());
                        map.put("avatar", user.getAvatar());
                    } else {
                        map.put("nickname", "匿名用户");
                        map.put("avatar", "");
                    }
                } else {
                    map.put("nickname", "匿名用户");
                    map.put("avatar", "");
                }

                map.put("title", (p.getIsUrgent() == 1 ? "【急需】" : "") + p.getTitle());
                map.put("content", p.getContent());
                map.put("img_urls", "");
                map.put("create_time", p.getCreateTime());

                if (p.getPrice() == null) {
                    map.put("extraInfo", "预算: 面议");
                } else {
                    map.put("extraInfo", "预算: ¥" + p.getPrice());
                }
                result.add(map);
            }

            // 3. 失物招领
            List<LostFound> lostList = lostFoundService.list(new LambdaQueryWrapper<LostFound>()
                    .eq(LostFound::getIsDeleted, 0)
                    .orderByDesc(LostFound::getCreateTime)
                    .last("LIMIT 100"));

            for (LostFound l : lostList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", l.getLostId());
                map.put("type", "lost");
                map.put("typeTag", "失物招领");
                map.put("userId", l.getUserId());

                if (l.getUserId() != null) {
                    User user = userService.getById(l.getUserId());
                    if (user != null) {
                        map.put("nickname", user.getNickName());
                        map.put("avatar", user.getAvatar());
                    } else {
                        map.put("nickname", "匿名用户");
                        map.put("avatar", "");
                    }
                } else {
                    map.put("nickname", "匿名用户");
                    map.put("avatar", "");
                }

                map.put("title", l.getGoodsName());
                map.put("content", l.getGoodsDesc());
                map.put("img_urls", l.getImgUrls() == null ? "" : l.getImgUrls());
                map.put("create_time", l.getCreateTime());
                map.put("extraInfo", null);
                result.add(map);
            }

            // 4. 闲置物品
            List<SecondHand> secondList = secondHandService.list(new LambdaQueryWrapper<SecondHand>()
                    .eq(SecondHand::getIsDeleted, 0)
                    .orderByDesc(SecondHand::getCreateTime)
                    .last("LIMIT 100"));

            for (SecondHand s : secondList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getSecondId());
                map.put("type", "second");
                map.put("typeTag", "闲置物品");
                map.put("userId", s.getUserId());

                if (s.getUserId() != null) {
                    User user = userService.getById(s.getUserId());
                    if (user != null) {
                        map.put("nickname", user.getNickName());
                        map.put("avatar", user.getAvatar());
                    } else {
                        map.put("nickname", "匿名用户");
                        map.put("avatar", "");
                    }
                } else {
                    map.put("nickname", "匿名用户");
                    map.put("avatar", "");
                }

                map.put("title", s.getGoodsName());
                map.put("content", s.getGoodsDesc());
                map.put("img_urls", s.getImgUrls() == null ? "" : s.getImgUrls());
                map.put("create_time", s.getCreateTime());

                // 价格处理（防止null）
                if (s.getPrice() == null) {
                    map.put("extraInfo", "价格: 面议");
                } else {
                    map.put("extraInfo", "价格: ¥" + s.getPrice());
                }

                // ========== 详情页需要的额外字段 ==========
                map.put("goodsDesc", s.getGoodsDesc());
                map.put("quality", s.getQuality());
                map.put("tradeAddress", s.getTradeAddress());

                result.add(map);
            }
        }
        else if ("news".equals(type)) {
            List<CampusNews> newsList = campusNewsService.list(new LambdaQueryWrapper<CampusNews>()
                    .eq(CampusNews::getIsDeleted, 0).orderByDesc(CampusNews::getCreateTime).last("LIMIT 100"));
            for (CampusNews news : newsList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", news.getNewsId());
                map.put("type", "news");
                map.put("typeTag", "动态");
                map.put("userId", news.getPublishId());

                if (news.getPublishId() != null) {
                    User user = userService.getById(news.getPublishId());
                    if (user != null) {
                        map.put("nickname", user.getNickName());
                        map.put("avatar", user.getAvatar());
                    } else {
                        map.put("nickname", "匿名用户");
                        map.put("avatar", "");
                    }
                } else {
                    map.put("nickname", "匿名用户");
                    map.put("avatar", "");
                }

                map.put("title", news.getTitle());
                map.put("content", news.getContent());
                map.put("img_urls", news.getImgUrls() == null ? "" : news.getImgUrls());
                map.put("create_time", news.getCreateTime());
                map.put("extraInfo", null);
                result.add(map);
            }
        }
        else if ("purchase".equals(type)) {
            List<PurchaseInfo> purchaseList = purchaseInfoService.list(new LambdaQueryWrapper<PurchaseInfo>()
                    .eq(PurchaseInfo::getIsDeleted, 0).orderByDesc(PurchaseInfo::getCreateTime).last("LIMIT 100"));
            for (PurchaseInfo p : purchaseList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getPurchaseId());
                map.put("type", "purchase");
                map.put("typeTag", "求购中");
                map.put("userId", p.getUserId());

                if (p.getUserId() != null) {
                    User user = userService.getById(p.getUserId());
                    if (user != null) {
                        map.put("nickname", user.getNickName());
                        map.put("avatar", user.getAvatar());
                    } else {
                        map.put("nickname", "匿名用户");
                        map.put("avatar", "");
                    }
                } else {
                    map.put("nickname", "匿名用户");
                    map.put("avatar", "");
                }

                map.put("title", (p.getIsUrgent() == 1 ? "【急需】" : "") + p.getTitle());
                map.put("content", p.getContent());
                map.put("img_urls", "");
                map.put("create_time", p.getCreateTime());

                if (p.getPrice() == null) {
                    map.put("extraInfo", "预算: 面议");
                } else {
                    map.put("extraInfo", "预算: ¥" + p.getPrice());
                }
                result.add(map);
            }
        }
        else if ("lost".equals(type)) {
            List<LostFound> lostList = lostFoundService.list(new LambdaQueryWrapper<LostFound>()
                    .eq(LostFound::getIsDeleted, 0).orderByDesc(LostFound::getCreateTime).last("LIMIT 100"));
            for (LostFound l : lostList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", l.getLostId());
                map.put("type", "lost");
                map.put("typeTag", "失物招领");
                map.put("userId", l.getUserId());

                if (l.getUserId() != null) {
                    User user = userService.getById(l.getUserId());
                    if (user != null) {
                        map.put("nickname", user.getNickName());
                        map.put("avatar", user.getAvatar());
                    } else {
                        map.put("nickname", "匿名用户");
                        map.put("avatar", "");
                    }
                } else {
                    map.put("nickname", "匿名用户");
                    map.put("avatar", "");
                }

                map.put("title", l.getGoodsName());
                map.put("content", l.getGoodsDesc());
                map.put("img_urls", l.getImgUrls() == null ? "" : l.getImgUrls());
                map.put("create_time", l.getCreateTime());
                map.put("extraInfo", null);
                result.add(map);
            }
        }
        else if ("second".equals(type)) {
            List<SecondHand> secondList = secondHandService.list(new LambdaQueryWrapper<SecondHand>()
                    .eq(SecondHand::getIsDeleted, 0).orderByDesc(SecondHand::getCreateTime).last("LIMIT 100"));
            for (SecondHand s : secondList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getSecondId());
                map.put("type", "second");
                map.put("typeTag", "闲置物品");
                map.put("userId", s.getUserId());

                if (s.getUserId() != null) {
                    User user = userService.getById(s.getUserId());
                    if (user != null) {
                        map.put("nickname", user.getNickName());
                        map.put("avatar", user.getAvatar());
                    } else {
                        map.put("nickname", "匿名用户");
                        map.put("avatar", "");
                    }
                } else {
                    map.put("nickname", "匿名用户");
                    map.put("avatar", "");
                }

                map.put("title", s.getGoodsName());
                map.put("content", s.getGoodsDesc());
                map.put("img_urls", s.getImgUrls() == null ? "" : s.getImgUrls());
                map.put("create_time", s.getCreateTime());

                if (s.getPrice() == null) {
                    map.put("extraInfo", "价格: 面议");
                } else {
                    map.put("extraInfo", "价格: ¥" + s.getPrice());
                }

                // 额外字段
                map.put("goodsDesc", s.getGoodsDesc());
                map.put("quality", s.getQuality());
                map.put("tradeAddress", s.getTradeAddress());

                result.add(map);
            }
        }

        result.sort((a, b) -> {
            LocalDateTime t1 = (LocalDateTime) a.get("create_time");
            LocalDateTime t2 = (LocalDateTime) b.get("create_time");
            return t2.compareTo(t1);
        });

        return Result.success(result);
    }
}