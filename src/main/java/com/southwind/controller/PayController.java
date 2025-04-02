package com.southwind.controller;

import com.southwind.config.AliPayConfig;
import com.southwind.config.WxPayConfig;
import com.southwind.entity.PayRecord;
import com.southwind.service.PayService;
import com.southwind.service.PayRecordService;
import com.southwind.vo.PayResultVO;
import com.southwind.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
public class PayController {

    @Autowired
    private PayService payService;

    @Autowired
    private PayRecordService payRecordService;

    @Autowired
    private AliPayConfig aliPayConfig;

    @Autowired
    private WxPayConfig wxPayConfig;

    /**
     * 创建支付宝支付订单
     */
    @PostMapping("/alipay/create")
    public Result<PayResultVO> createAlipayOrder(@RequestBody Map<String, String> params) {
        PayResultVO result = payService.createAlipayOrder(
            Integer.parseInt(params.get("payRecordId")),
            params.get("outTradeNo"),
            params.get("totalAmount"),
            params.get("subject")
        );
        return Result.success(result);
    }

    /**
     * 创建微信支付订单
     */
    @PostMapping("/wxpay/create")
    public Result<PayResultVO> createWxpayOrder(@RequestBody Map<String, String> params) {
        PayResultVO result = payService.createWxpayOrder(
            Integer.parseInt(params.get("payRecordId")),
            params.get("outTradeNo"),
            params.get("totalAmount"),
            params.get("body"),
            params.get("openid")
        );
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

    /**
     * 更新支付状态
     */
    @PostMapping("/record/updateStatus")
    public Result<Void> updatePayStatus(@RequestBody PayRecord payRecord) {
        PayRecord record = payRecordService.getById(payRecord.getPayRecordId());
        if (record != null) {
            record.setPayStatus(payRecord.getPayStatus());
            record.setPayMethod(payRecord.getPayMethod());
            record.setTradeNo(payRecord.getTradeNo());
            record.setPayTime(LocalDateTime.now());

            if (payRecordService.updateById(record)) {
                return Result.success();
            }
        }
        return Result.error("更新支付状态失败");
    }

    /**
     * 获取支付宝配置
     */
    @GetMapping("/alipay/config")
    public Result<AliPayConfig> getAlipayConfig() {
        return Result.success(aliPayConfig);
    }

    /**
     * 更新支付宝配置
     */
    @PostMapping("/alipay/config")
    public Result<Void> updateAlipayConfig(@RequestBody AliPayConfig config) {
        // 更新配置
        aliPayConfig.setAppId(config.getAppId());
        aliPayConfig.setGateway(config.getGateway());
        aliPayConfig.setPrivateKey(config.getPrivateKey());
        aliPayConfig.setPublicKey(config.getPublicKey());
        aliPayConfig.setSignType(config.getSignType());
        aliPayConfig.setCharset(config.getCharset());
        aliPayConfig.setFormat(config.getFormat());
        aliPayConfig.setNotifyUrl(config.getNotifyUrl());
        aliPayConfig.setReturnUrl(config.getReturnUrl());

        // 保存到数据库或配置文件
        // 这里需要实现配置的持久化存储

        return Result.success();
    }

    /**
     * 获取微信支付配置
     */
    @GetMapping("/wxpay/config")
    public Result<WxPayConfig> getWxpayConfig() {
        return Result.success(wxPayConfig);
    }

    /**
     * 更新微信支付配置
     */
    @PostMapping("/wxpay/config")
    public Result<Void> updateWxpayConfig(@RequestBody WxPayConfig config) {
        // 更新配置
        wxPayConfig.setAppId(config.getAppId());
        wxPayConfig.setMchId(config.getMchId());
        wxPayConfig.setMchKey(config.getMchKey());
        wxPayConfig.setNotifyUrl(config.getNotifyUrl());

        // 保存到数据库或配置文件
        // 这里需要实现配置的持久化存储

        return Result.success();
    }
}
