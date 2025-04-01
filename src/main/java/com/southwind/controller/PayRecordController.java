package com.southwind.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.PayRecord;
import com.southwind.form.PayListForm;
import com.southwind.service.PayRecordService;
import com.southwind.vo.PageVO;
import com.southwind.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2023-12-13
 */
@RestController
@RequestMapping("/payRecord")
public class PayRecordController {

    @Autowired
    private PayRecordService payRecordService;

    @GetMapping("/list")
    public Result list(PayListForm payListForm){
        PageVO pageVO = payRecordService.payList(payListForm);
        return Result.success(pageVO);
    }

    /**
     * 新的列表查询方法，使用新的查询方式
     */
    @GetMapping("/listNew")
    public Result<Map<String, Object>> listNew(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "limit", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "propertyId", required = false) Integer propertyId,
            @RequestParam(value = "parkId", required = false) Integer parkId,
            @RequestParam(value = "number", required = false) String number) {
        
        // 构建查询条件
        LambdaQueryWrapper<PayRecord> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (propertyId != null) {
            queryWrapper.eq(PayRecord::getPropertyId, propertyId);
        }
        
        if (parkId != null) {
            queryWrapper.eq(PayRecord::getParkId, parkId);
        }
        
        if (number != null && !number.isEmpty()) {
            queryWrapper.like(PayRecord::getNumber, number);
        }
        
        // 按创建时间排序
        queryWrapper.orderByDesc(PayRecord::getCreateTime);
        
        // 分页查询
        Page<PayRecord> page = new Page<>(pageNum, pageSize);
        Page<PayRecord> payRecordPage = payRecordService.page(page, queryWrapper);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", payRecordPage.getRecords());
        result.put("total", payRecordPage.getTotal());
        
        return Result.success(result);
    }
}

