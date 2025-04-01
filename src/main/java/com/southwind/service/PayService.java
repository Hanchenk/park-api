package com.southwind.service;

import java.util.Map;

import com.southwind.vo.PayResultVO;

public interface PayService {
    /**
     * 创建支付宝支付订单
     * @param payRecordId 支付记录ID
     * @param outTradeNo 订单号
     * @param totalAmount 支付金额
     * @param subject 订单标题
     * @return 支付结果
     */
    PayResultVO createAlipayOrder(Integer payRecordId, String outTradeNo, String totalAmount, String subject);
    
    /**
     * 创建微信支付订单
     * @param payRecordId 支付记录ID
     * @param outTradeNo 订单号
     * @param totalAmount 支付金额
     * @param body 订单描述
     * @param openid 用户openid
     * @return 支付结果
     */
    PayResultVO createWxpayOrder(Integer payRecordId, String outTradeNo, String totalAmount, String body, String openid);
    
    /**
     * 处理支付宝回调
     * @param params 回调参数
     * @return 处理结果
     */
    String handleAlipayNotify(Map<String, String> params);
    
    /**
     * 处理微信支付回调
     * @param xmlData 回调XML数据
     * @return 处理结果
     */
    String handleWxpayNotify(String xmlData);
    
    /**
     * 查询支付宝订单状态
     * @param outTradeNo 订单号
     * @return 订单状态
     */
    Map<String, Object> queryAlipayOrder(String outTradeNo);
    
    /**
     * 查询微信订单状态
     * @param outTradeNo 订单号
     * @return 订单状态
     */
    Map<String, Object> queryWxpayOrder(String outTradeNo);
} 