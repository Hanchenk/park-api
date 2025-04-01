package com.southwind.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AliPayConfig {
    // 应用ID
    private String appId;
    // 商户私钥
    private String privateKey;
    // 支付宝公钥
    private String publicKey;
    // 服务网关
    private String gateway;
    // 异步通知地址
    private String notifyUrl;
    // 同步返回地址
    private String returnUrl;
    // 签名方式
    private String signType;
    // 编码格式
    private String charset;
    // 支付宝格式
    private String format;
} 