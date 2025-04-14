package com.southwind.service;

import com.southwind.entity.InOutRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.southwind.form.InOutQueryForm;
import com.southwind.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2023-07-26
 */
public interface InOutRecordService extends IService<InOutRecord> {
    public Map chart();
    public PageVO inOutRecordList(InOutQueryForm inOutQueryForm);
    
    /**
     * 获取今日车流量趋势数据
     * @return 包含入场和出场数据的Map
     */
    Map<String, Object> getDailyTrafficTrend();

    /**
     * 获取各停车场剩余车位信息
     * @return 各停车场的剩余车位信息
     */
    List<Map<String, Object>> getParkingSpacesInfo();
}
