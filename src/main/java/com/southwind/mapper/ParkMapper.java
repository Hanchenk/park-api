package com.southwind.mapper;

import com.southwind.entity.Park;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2023-12-12
 */
public interface ParkMapper extends BaseMapper<Park> {

    @Select({
            "select park_name from park where park_id = #{id}"
    })
    public String getNameById(Integer id);

    @Select({
            "select monthly_price from park where park_id = #{id}"
    })
    public Integer getMonthlyPrice(Integer id);

    /**
     * 查询所有停车场的基本信息
     * @return 停车场信息列表
     */
    @Select("SELECT park_id as parkId, park_name as parkName, total_spaces as totalSpaces " +
            "FROM park ORDER BY park_id")
    List<Map<String, Object>> selectParkingSpacesInfo();

}
