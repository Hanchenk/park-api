package com.southwind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信支付配置实体类
 */
@Data
@TableName("wxpay_config")
public class WxpayConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 商户号
     */
    private String mchId;
    
    /**
     * 商户密钥
     */
    private String mchKey;
    
    /**
     * 回调地址
     */
    private String notifyUrl;
    
    /**
     * 交易类型
     */
    private String tradeType;
    
    /**
     * 证书路径
     */
    private String certPath;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
} 