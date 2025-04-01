package com.southwind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付宝配置实体类
 */
@Data
@TableName("alipay_config")
public class AlipayConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 商户私钥
     */
    private String privateKey;
    
    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;
    
    /**
     * 回调地址
     */
    private String notifyUrl;
    
    /**
     * 网关地址
     */
    private String gatewayUrl;
    
    /**
     * 签名方式
     */
    private String signType;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
} 