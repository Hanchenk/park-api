package com.southwind.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.southwind.entity.InOutRecord;
import com.southwind.vo.ChartVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2023-07-26
 */
@Mapper
public interface InOutRecordMapper extends BaseMapper<InOutRecord> {

    @Select({
            "select sum(amount) value,p.park_name name from park p,pay_record pr where p.park_id = pr.park_id group by pr.park_id"
    })
    public List<ChartVO> chart();

    /**
     * 获取车辆最近一次未出场的记录
     */
    @Select("SELECT * FROM in_out_record WHERE park_id = #{inOutRecord.parkId} AND number = #{inOutRecord.number} AND out_time IS NULL ORDER BY in_time DESC LIMIT 1")
    InOutRecord getInOutRecord(@Param("inOutRecord") InOutRecord inOutRecord);

}
