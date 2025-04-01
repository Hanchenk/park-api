package com.southwind.controller;

import com.southwind.service.PayService;
import com.southwind.vo.PayResultVO;
import com.southwind.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
public class PayController {

    @Autowired
    private PayService payService;
    
    /**
     * 创建支付宝支付订单
     */
    @PostMapping("/alipay/create")
    public Result<PayResultVO> createAlipayOrder(@RequestParam Integer payRecordId,
                                                @RequestParam String outTradeNo,
                                                @RequestParam String totalAmount,
                                                @RequestParam String subject) {
        PayResultVO result = payService.createAlipayOrder(payRecordId, outTradeNo, totalAmount, subject);
        return Result.success(result);
    }
    
    /**
     * 创建微信支付订单
     */
    @PostMapping("/wxpay/create")
    public Result<PayResultVO> createWxpayOrder(@RequestParam Integer payRecordId,
                                               @RequestParam String outTradeNo,
                                               @RequestParam String totalAmount,
                                               @RequestParam String body,
                                               @RequestParam String openid) {
        PayResultVO result = payService.createWxpayOrder(payRecordId, outTradeNo, totalAmount, body, openid);
        return Result.success(result);
    }
    
    /**
     * 支付宝支付结果通知
     */
    @PostMapping("/alipay/notify")
    public String alipayNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        
        return payService.handleAlipayNotify(params);
    }
    
    /**
     * 微信支付结果通知
     */
    @PostMapping("/wxpay/notify")
    public String wxpayNotify(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        
        return payService.handleWxpayNotify(sb.toString());
    }
    
    /**
     * 查询支付宝订单状态
     */
    @GetMapping("/alipay/query")
    public Result<Map<String, Object>> queryAlipayOrder(@RequestParam String outTradeNo) {
        Map<String, Object> result = payService.queryAlipayOrder(outTradeNo);
        return Result.success(result);
    }
    
    /**
     * 查询微信支付订单状态
     */
    @GetMapping("/wxpay/query")
    public Result<Map<String, Object>> queryWxpayOrder(@RequestParam String outTradeNo) {
        Map<String, Object> result = payService.queryWxpayOrder(outTradeNo);
        return Result.success(result);
    }
} 