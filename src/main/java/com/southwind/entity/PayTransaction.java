package com.southwind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 支付交易记录实体类
 * </p>
 *
 * @author admin
 */
@Data
@TableName("pay_transaction")
@EqualsAndHashCode(callSuper = false)
public class PayTransaction implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 关联的支付记录ID
    private Integer payRecordId;
    
    // 商户订单号
    private String outTradeNo;
    
    // 支付宝/微信交易号
    private String tradeNo;
    
    // 支付类型：alipay, wxpay
    private String payType;
    
    // 支付金额
    private String totalAmount;
    
    // 订单标题/描述
    private String subject;
    
    // 支付状态：0-待支付，1-支付成功，2-支付失败
    private Integer payStatus;
    
    // 创建时间
    private LocalDateTime createTime;
    
    // 支付时间
    private LocalDateTime payTime;
} 