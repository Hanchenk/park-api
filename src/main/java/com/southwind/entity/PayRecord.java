package com.southwind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 支付记录实体类
 * </p>
 *
 * @author admin
 * @since 2023-12-13
 */
@Data
@TableName("pay_record")
@EqualsAndHashCode(callSuper = false)
public class PayRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "pay_record_id", type = IdType.AUTO)
    private Integer payRecordId;

    private Integer propertyId;

    private Integer parkId;

    private String number;

    /**
     * 1:临时车
     * 2:包月车
     * 3:VIP
     */
    private Integer payType;

    private Integer amount;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    // 以下是新增的支付相关字段
    
    // 商户订单号
    private String outTradeNo;
    
    // 支付宝/微信交易号
    private String tradeNo;
    
    // 支付方式：alipay, wxpay
    private String payMethod;
    
    // 支付状态：0-待支付，1-支付成功，2-支付失败
    private Integer payStatus;
    
    // 支付时间
    private LocalDateTime payTime;
}
