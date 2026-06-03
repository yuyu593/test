package org.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.CollectInfo;
import org.example.vo.CollectVo;
import java.util.List;

public interface CollectInfoMapper {

    // 添加收藏
    @Insert("INSERT INTO collect_info(user_id, target_id, target_type) " +
            "VALUES(#{userId}, #{targetId}, #{targetType})")
    int insert(CollectInfo collect);

    // 取消收藏
    @Delete("DELETE FROM collect_info WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}")
    int delete(@Param("userId") Long userId,
               @Param("targetId") Long targetId,
               @Param("targetType") Integer targetType);

    // 检查是否已收藏
    @Select("SELECT count(*) FROM collect_info " +
            "WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}")
    int countExists(@Param("userId") Long userId,
                    @Param("targetId") Long targetId,
                    @Param("targetType") Integer targetType);

    // 统计内容收藏数
    @Select("SELECT count(*) FROM collect_info WHERE target_id = #{targetId} AND target_type = #{targetType}")
    int countByTarget(@Param("targetId") Long targetId,
                      @Param("targetType") Integer targetType);

    // ========== 联表查询 用户收藏 + 对应内容标题/正文 ==========
    // 1. 收藏 - 闲置物品 type=1（表：second_hand，主键：second_id；标题：goods_name；内容：goods_desc）
    @Select("SELECT c.collect_id, c.user_id, c.target_id, c.target_type, c.collect_time, " +
            "s.goods_name AS title, s.goods_desc AS content " +
            "FROM collect_info c " +
            "LEFT JOIN second_hand s ON c.target_id = s.second_id " +
            "WHERE c.user_id = #{userId} AND c.target_type = 1 " +
            "ORDER BY c.collect_time DESC")
    List<CollectVo> listCollectSecond(@Param("userId") Long userId);

    // 2. 收藏 - 求购信息 type=2（表：purchase_info，主键：purchase_id；标题：title；内容：content）
    @Select("SELECT c.collect_id, c.user_id, c.target_id, c.target_type, c.collect_time, " +
            "p.title, p.content " +
            "FROM collect_info c " +
            "LEFT JOIN purchase_info p ON c.target_id = p.purchase_id " +
            "WHERE c.user_id = #{userId} AND c.target_type = 2 " +
            "ORDER BY c.collect_time DESC")
    List<CollectVo> listCollectPurchase(@Param("userId") Long userId);

    // 3. 收藏 - 校园动态 type=3（表：campus_news，主键：news_id；标题：title；内容：content）
    @Select("SELECT c.collect_id, c.user_id, c.target_id, c.target_type, c.collect_time, " +
            "n.title, n.content " +
            "FROM collect_info c " +
            "LEFT JOIN campus_news n ON c.target_id = n.news_id " +
            "WHERE c.user_id = #{userId} AND c.target_type = 3 " +
            "ORDER BY c.collect_time DESC")
    List<CollectVo> listCollectNews(@Param("userId") Long userId);

    // 4. 收藏 - 失物招领 type=4（表：lost_found，主键：lost_id；标题：goods_name；内容：goods_desc）
    @Select("SELECT c.collect_id, c.user_id, c.target_id, c.target_type, c.collect_time, " +
            "l.goods_name AS title, l.goods_desc AS content " +
            "FROM collect_info c " +
            "LEFT JOIN lost_found l ON c.target_id = l.lost_id " +
            "WHERE c.user_id = #{userId} AND c.target_type = 4 " +
            "ORDER BY c.collect_time DESC")
    List<CollectVo> listCollectLost(@Param("userId") Long userId);
}