package com.southwind.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.southwind.entity.PayRecord;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2023-12-13
 */
@Mapper
public interface PayRecordMapper extends BaseMapper<PayRecord> {

    /**
     * 查询各停车场的收入统计
     * @param date 日期字符串，格式为 yyyy-MM-dd
     * @return 各停车场的收入列表
     */
    @Select("SELECT p.park_name as parkName, IFNULL(SUM(pr.amount), 0) as income " +
            "FROM park p " +
            "LEFT JOIN pay_record pr ON p.park_id = pr.park_id " +
            "AND DATE_FORMAT(pr.create_time, '%Y-%m-%d') = #{date} " +
            "GROUP BY p.park_id, p.park_name " +
            "ORDER BY income DESC")
    List<Map<String, Object>> selectIncomeByPark(@Param("date") String date);

}
