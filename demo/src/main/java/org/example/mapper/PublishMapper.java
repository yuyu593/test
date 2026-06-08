package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.vo.PublishVO;
import java.util.List;

@Mapper
public interface PublishMapper {

    @Select("SELECT second_id targetId,1 targetType,goods_name title,goods_desc content,create_time FROM second_hand WHERE user_id=#{userId} AND is_deleted=0 " +
            "UNION ALL " +
            "SELECT purchase_id targetId,2 targetType,title,content,create_time FROM purchase_info WHERE user_id=#{userId} AND is_deleted=0 " +
            "UNION ALL " +
            "SELECT news_id targetId,3 targetType,title,content,create_time FROM campus_news WHERE publish_id=#{userId} AND is_deleted=0 " +
            "UNION ALL " +
            "SELECT lost_id targetId,4 targetType,goods_name title,goods_desc content,create_time FROM lost_found WHERE user_id=#{userId} AND is_deleted=0 " +
            "ORDER BY create_time DESC")
    List<PublishVO> selectAllByUserId(@Param("userId") Long userId);
}