package com.southwind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayResultVO {
    // 支付类型：alipay, wxpay
    private String payType;
    
    // 订单号
    private String outTradeNo;
    
    // 支付状态：0-待支付，1-支付成功，2-支付失败
    private Integer payStatus;
    
    // 支付金额
    private String totalAmount;
    
    // 支付参数（支付宝返回表单，微信返回支付参数）
    private String payParams;
    
    // 错误信息
    private String errorMsg;
} 