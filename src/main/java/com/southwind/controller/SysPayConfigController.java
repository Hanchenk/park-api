package com.southwind.controller;

import com.southwind.entity.AlipayConfig;
import com.southwind.entity.WxpayConfig;
import com.southwind.service.PayConfigService;
import com.southwind.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 支付配置控制器
 */
@RestController
@RequestMapping("/sys/pay/config")
public class SysPayConfigController {

    @Autowired
    private PayConfigService payConfigService;

    /**
     * 获取支付宝配置
     */
    @GetMapping("/alipay")
    public Result<AlipayConfig> getAlipayConfig() {
        AlipayConfig config = payConfigService.getAlipayConfig();
        return Result.success(config);
    }

    /**
     * 更新支付宝配置
     */
    @PutMapping("/alipay")
    public Result<Void> updateAlipayConfig(@RequestBody AlipayConfig config) {
        payConfigService.updateAlipayConfig(config);
        return Result.success();
    }

    /**
     * 获取微信支付配置
     */
    @GetMapping("/wxpay")
    public Result<WxpayConfig> getWxpayConfig() {
        WxpayConfig config = payConfigService.getWxpayConfig();
        return Result.success(config);
    }

    /**
     * 更新微信支付配置
     */
    @PutMapping("/wxpay")
    public Result<Void> updateWxpayConfig(@RequestBody WxpayConfig config) {
        payConfigService.updateWxpayConfig(config);
        return Result.success();
    }
} 