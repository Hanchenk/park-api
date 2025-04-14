package com.southwind.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.southwind.entity.PayRecord;
import com.southwind.form.PayListForm;
import com.southwind.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 支付记录服务接口
 */
public interface PayRecordService extends IService<PayRecord> {
    /**
     * 分页查询支付记录
     * @param payListForm 查询条件
     * @return 分页结果
     */
    PageVO payList(PayListForm payListForm);
    
    /**
     * 获取各停车场的收入统计
     * @param date 日期字符串，格式为 yyyy-MM-dd
     * @return 各停车场的收入列表
     */
    List<Map<String, Object>> getIncomeByPark(String date);
}
