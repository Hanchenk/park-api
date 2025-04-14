package com.southwind.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.southwind.entity.InOutRecord;
import com.southwind.entity.PayRecord;
import com.southwind.service.InOutRecordService;
import com.southwind.service.PayRecordService;
import com.southwind.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping("/sys/dashboard")
public class DashboardController {

    @Autowired
    private InOutRecordService inOutRecordService;
    
    @Autowired
    private PayRecordService payRecordService;
    
    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    public Result getStats() {
        Map<String, Object> statsMap = new HashMap<>();
        
        // 获取今天的日期字符串，格式为 yyyy-MM-dd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        
        // 今日入场车辆数
        QueryWrapper<InOutRecord> inQueryWrapper = new QueryWrapper<>();
        inQueryWrapper.apply("DATE_FORMAT(in_time, '%Y-%m-%d') = {0}", today);
        int todayIn = inOutRecordService.count(inQueryWrapper);
        
        // 今日出场车辆数
        QueryWrapper<InOutRecord> outQueryWrapper = new QueryWrapper<>();
        outQueryWrapper.apply("DATE_FORMAT(out_time, '%Y-%m-%d') = {0}", today);
        outQueryWrapper.isNotNull("out_time");
        int todayOut = inOutRecordService.count(outQueryWrapper);
        
        // 当前在场车辆数
        QueryWrapper<InOutRecord> currentInQueryWrapper = new QueryWrapper<>();
        currentInQueryWrapper.isNull("out_time");
        int currentIn = inOutRecordService.count(currentInQueryWrapper);
        
        // 今日收入 - 修复查询条件，确保只统计今日的收入
        QueryWrapper<PayRecord> incomeQueryWrapper = new QueryWrapper<>();
        // 使用 pay_time 而不是 create_time 来过滤今日支付的记录
        incomeQueryWrapper.apply("DATE_FORMAT(pay_time, '%Y-%m-%d') = {0}", today);
        // 只统计支付状态为已支付的记录
        incomeQueryWrapper.eq("pay_status", 1);
        incomeQueryWrapper.select("IFNULL(SUM(amount), 0) as total_income");
        Map<String, Object> incomeMap = payRecordService.getMap(incomeQueryWrapper);
        
        // 防止空指针异常
        BigDecimal todayIncome = BigDecimal.ZERO;
        if (incomeMap != null && incomeMap.get("total_income") != null) {
            todayIncome = new BigDecimal(incomeMap.get("total_income").toString());
        }
        
        // 组装结果
        statsMap.put("todayIn", todayIn);
        statsMap.put("todayOut", todayOut);
        statsMap.put("currentIn", currentIn);
        statsMap.put("todayIncome", todayIncome);
        
        return Result.ok().put("data", statsMap);
    }

    /**
     * 获取今日车流量趋势
     */
    @GetMapping("/trafficTrend")
    public Result getTrafficTrend() {
        try {
            Map<String, Object> trendData = inOutRecordService.getDailyTrafficTrend();
            return Result.ok().put("data", trendData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取车流量趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取停车场收入统计
     */
    @GetMapping("/income")
    public Result getIncomeStats() {
        try {
            // 获取今天的日期字符串，格式为 yyyy-MM-dd
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new Date());
            
            // 查询各停车场的收入
            List<Map<String, Object>> incomeByPark = payRecordService.getIncomeByPark(today);
            
            // 准备返回数据
            List<String> parkNames = new ArrayList<>();
            List<BigDecimal> incomeData = new ArrayList<>();
            
            for (Map<String, Object> item : incomeByPark) {
                parkNames.add((String) item.get("parkName"));
                incomeData.add((BigDecimal) item.get("income"));
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("parkNames", parkNames);
            result.put("incomeData", incomeData);
            
            return Result.ok().put("data", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取停车场收入统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取各停车场剩余车位信息
     */
    @GetMapping("/parkingSpaces")
    public Result getParkingSpaces() {
        try {
            List<Map<String, Object>> parkingSpaces = inOutRecordService.getParkingSpacesInfo();
            return Result.ok().put("data", parkingSpaces);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取停车场剩余车位信息失败: " + e.getMessage());
        }
    }
} 