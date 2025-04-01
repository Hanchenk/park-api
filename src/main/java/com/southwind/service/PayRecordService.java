package com.southwind.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.southwind.entity.PayRecord;
import com.southwind.form.PayListForm;
import com.southwind.vo.PageVO;

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
}
