package com.southwind.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wxpay")
public class WxPayConfig {
    // 公众号ID
    private String appId;
    // 商户号
    private String mchId;
    // 商户密钥
    private String mchKey;
    // 回调地址
    private String notifyUrl;
    // 交易类型
    private String tradeType;
} 