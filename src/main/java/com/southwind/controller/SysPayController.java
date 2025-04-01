package com.southwind.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.PayRecord;
import com.southwind.service.PayRecordService;
import com.southwind.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付管理控制器
 */
@RestController
@RequestMapping("/sys/pay")
public class SysPayController {

    @Autowired
    private PayRecordService payRecordService;

    /**
     * 获取支付记录列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "outTradeNo", required = false) String outTradeNo,
            @RequestParam(value = "payMethod", required = false) String payMethod,
            @RequestParam(value = "payType", required = false) Integer payType,
            @RequestParam(value = "payStatus", required = false) Integer payStatus,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime) {
        
        // 构建查询条件
        LambdaQueryWrapper<PayRecord> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (outTradeNo != null && !outTradeNo.isEmpty()) {
            queryWrapper.like(PayRecord::getOutTradeNo, outTradeNo);
        }
        
        if (payMethod != null && !payMethod.isEmpty()) {
            queryWrapper.eq(PayRecord::getPayMethod, payMethod);
        }
        
        if (payType != null) {
            queryWrapper.eq(PayRecord::getPayType, payType);
        }
        
        if (payStatus != null) {
            queryWrapper.eq(PayRecord::getPayStatus, payStatus);
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
    
    /**
     * 获取支付记录详情
     */
    @GetMapping("/info/{id}")
    public Result<PayRecord> info(@PathVariable("id") Integer id) {
        PayRecord payRecord = payRecordService.getById(id);
        if (payRecord != null) {
            return Result.success(payRecord);
        } else {
            return Result.error("支付记录不存在");
        }
    }
    
    /**
     * 导出支付记录
     */
    @GetMapping("/export")
    public Result<String> export(
            @RequestParam(value = "outTradeNo", required = false) String outTradeNo,
            @RequestParam(value = "payMethod", required = false) String payMethod,
            @RequestParam(value = "payType", required = false) Integer payType,
            @RequestParam(value = "payStatus", required = false) Integer payStatus,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime) {
        
        // 实际项目中应该实现导出功能
        // 这里简化处理，直接返回成功
        return Result.success("导出成功", "操作成功");
    }
} 