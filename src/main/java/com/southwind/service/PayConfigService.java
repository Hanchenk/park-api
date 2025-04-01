package com.southwind.service;

import com.southwind.entity.AlipayConfig;
import com.southwind.entity.WxpayConfig;

/**
 * 支付配置服务接口
 */
public interface PayConfigService {
    
    /**
     * 获取支付宝配置
     */
    AlipayConfig getAlipayConfig();
    
    /**
     * 更新支付宝配置
     */
    void updateAlipayConfig(AlipayConfig config);
    
    /**
     * 获取微信支付配置
     */
    WxpayConfig getWxpayConfig();
    
    /**
     * 更新微信支付配置
     */
    void updateWxpayConfig(WxpayConfig config);
} 