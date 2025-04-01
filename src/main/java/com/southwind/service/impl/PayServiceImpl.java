package com.southwind.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.southwind.config.AliPayConfig;
import com.southwind.config.WxPayConfig;
import com.southwind.entity.PayRecord;
import com.southwind.entity.PayTransaction;
import com.southwind.service.PayRecordService;
import com.southwind.service.PayService;
import com.southwind.vo.PayResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private AliPayConfig aliPayConfig;
    
    @Autowired
    private WxPayConfig wxPayConfig;
    
    @Autowired
    private PayRecordService payRecordService;
    
    @Override
    @Transactional
    public PayResultVO createAlipayOrder(Integer payRecordId, String outTradeNo, String totalAmount, String subject) {
        try {
            // 创建支付宝客户端
            AlipayClient alipayClient = new DefaultAlipayClient(
                    aliPayConfig.getGateway(),
                    aliPayConfig.getAppId(),
                    aliPayConfig.getPrivateKey(),
                    aliPayConfig.getFormat(),
                    aliPayConfig.getCharset(),
                    aliPayConfig.getPublicKey(),
                    aliPayConfig.getSignType()
            );
            
            // 创建API请求对象
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setReturnUrl(aliPayConfig.getReturnUrl());
            request.setNotifyUrl(aliPayConfig.getNotifyUrl());
            
            // 设置业务参数
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(outTradeNo);
            model.setTotalAmount(totalAmount);
            model.setSubject(subject);
            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            request.setBizModel(model);
            
            // 发送请求，获取支付表单
            String form = alipayClient.pageExecute(request).getBody();
            
            // 更新支付记录
            PayRecord payRecord = payRecordService.getById(payRecordId);
            if (payRecord != null) {
                payRecord.setOutTradeNo(outTradeNo);
                payRecord.setPayMethod("alipay");
                payRecord.setPayStatus(0); // 待支付
                payRecordService.updateById(payRecord);
            }
            
            return new PayResultVO("alipay", outTradeNo, 0, totalAmount, form, null);
        } catch (AlipayApiException e) {
            log.error("创建支付宝订单失败", e);
            return new PayResultVO("alipay", outTradeNo, 2, totalAmount, null, e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public PayResultVO createWxpayOrder(Integer payRecordId, String outTradeNo, String totalAmount, String body, String openid) {
        try {
            // 构建微信支付参数
            SortedMap<String, String> params = new TreeMap<>();
            params.put("appid", wxPayConfig.getAppId());
            params.put("mch_id", wxPayConfig.getMchId());
            params.put("nonce_str", generateNonceStr());
            params.put("body", body);
            params.put("out_trade_no", outTradeNo);
            // 微信支付金额单位为分，需要转换
            int amount = (int)(Float.parseFloat(totalAmount) * 100);
            params.put("total_fee", String.valueOf(amount));
            params.put("spbill_create_ip", "127.0.0.1");
            params.put("notify_url", wxPayConfig.getNotifyUrl());
            params.put("trade_type", wxPayConfig.getTradeType());
            params.put("openid", openid);
            
            // 生成签名
            String sign = generateWxSign(params, wxPayConfig.getMchKey());
            params.put("sign", sign);
            
            // 转换为XML
            String xmlData = mapToXml(params);
            
            // 发送请求到微信支付API
            // 这里需要使用HTTP客户端发送请求，简化起见，假设已经获取到了返回结果
            String result = ""; // 实际项目中需要发送HTTP请求获取结果
            
            // 解析返回结果
            Map<String, String> resultMap = xmlToMap(result);
            
            if ("SUCCESS".equals(resultMap.get("return_code")) && "SUCCESS".equals(resultMap.get("result_code"))) {
                // 获取预支付ID
                String prepayId = resultMap.get("prepay_id");
                
                // 构建JSAPI支付参数
                Map<String, String> jsapiParams = new HashMap<>();
                jsapiParams.put("appId", wxPayConfig.getAppId());
                jsapiParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                jsapiParams.put("nonceStr", generateNonceStr());
                jsapiParams.put("package", "prepay_id=" + prepayId);
                jsapiParams.put("signType", "MD5");
                jsapiParams.put("paySign", generateWxSign(jsapiParams, wxPayConfig.getMchKey()));
                
                // 更新支付记录
                PayRecord payRecord = payRecordService.getById(payRecordId);
                if (payRecord != null) {
                    payRecord.setOutTradeNo(outTradeNo);
                    payRecord.setPayMethod("wxpay");
                    payRecord.setPayStatus(0); // 待支付
                    payRecordService.updateById(payRecord);
                }
                
                return new PayResultVO("wxpay", outTradeNo, 0, totalAmount, JSON.toJSONString(jsapiParams), null);
            } else {
                return new PayResultVO("wxpay", outTradeNo, 2, totalAmount, null, resultMap.get("return_msg"));
            }
        } catch (Exception e) {
            log.error("创建微信支付订单失败", e);
            return new PayResultVO("wxpay", outTradeNo, 2, totalAmount, null, e.getMessage());
        }
    }
    
    @Override
    public String handleAlipayNotify(Map<String, String> params) {
        try {
            // 验证签名
            // 实际项目中需要验证支付宝的签名
            boolean signVerified = true;
            
            if (signVerified) {
                // 商户订单号
                String outTradeNo = params.get("out_trade_no");
                // 支付宝交易号
                String tradeNo = params.get("trade_no");
                // 交易状态
                String tradeStatus = params.get("trade_status");
                
                // 查询支付记录
                PayRecord payRecord = payRecordService.lambdaQuery()
                        .eq(PayRecord::getOutTradeNo, outTradeNo)
                        .one();
                
                if (payRecord != null) {
                    // 交易成功
                    if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                        if (payRecord.getPayStatus() != 1) {
                            payRecord.setPayStatus(1); // 支付成功
                            payRecord.setTradeNo(tradeNo);
                            payRecord.setPayTime(LocalDateTime.now());
                            payRecordService.updateById(payRecord);
                        }
                        return "success";
                    } else {
                        // 交易失败
                        payRecord.setPayStatus(2); // 支付失败
                        payRecordService.updateById(payRecord);
                        return "fail";
                    }
                }
            } else {
                return "fail";
            }
        } catch (Exception e) {
            log.error("处理支付宝回调失败", e);
        }
        return "fail";
    }
    
    @Override
    public String handleWxpayNotify(String xmlData) {
        try {
            // 解析XML数据
            Map<String, String> params = xmlToMap(xmlData);
            
            // 验证签名
            // 实际项目中需要验证微信支付的签名
            boolean signVerified = true;
            
            if (signVerified && "SUCCESS".equals(params.get("return_code")) && "SUCCESS".equals(params.get("result_code"))) {
                // 商户订单号
                String outTradeNo = params.get("out_trade_no");
                // 微信交易号
                String transactionId = params.get("transaction_id");
                
                // 查询支付记录
                PayRecord payRecord = payRecordService.lambdaQuery()
                        .eq(PayRecord::getOutTradeNo, outTradeNo)
                        .one();
                
                if (payRecord != null && payRecord.getPayStatus() != 1) {
                    payRecord.setPayStatus(1); // 支付成功
                    payRecord.setTradeNo(transactionId);
                    payRecord.setPayTime(LocalDateTime.now());
                    payRecordService.updateById(payRecord);
                    
                    // 返回成功结果
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("return_code", "SUCCESS");
                    resultMap.put("return_msg", "OK");
                    return mapToXml(resultMap);
                }
            }
            
            // 返回失败结果
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("return_code", "FAIL");
            resultMap.put("return_msg", "处理失败");
            return mapToXml(resultMap);
        } catch (Exception e) {
            log.error("处理微信支付回调失败", e);
            
            // 返回失败结果
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("return_code", "FAIL");
            resultMap.put("return_msg", e.getMessage());
            return mapToXml(resultMap);
        }
    }
    
    @Override
    public Map<String, Object> queryAlipayOrder(String outTradeNo) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 创建支付宝客户端
            AlipayClient alipayClient = new DefaultAlipayClient(
                    aliPayConfig.getGateway(),
                    aliPayConfig.getAppId(),
                    aliPayConfig.getPrivateKey(),
                    aliPayConfig.getFormat(),
                    aliPayConfig.getCharset(),
                    aliPayConfig.getPublicKey(),
                    aliPayConfig.getSignType()
            );
            
            // 创建查询请求
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", outTradeNo);
            request.setBizContent(bizContent.toString());
            
            // 执行查询
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            
            if (response.isSuccess()) {
                result.put("success", true);
                result.put("tradeStatus", response.getTradeStatus());
                result.put("tradeNo", response.getTradeNo());
                
                // 如果支付成功，更新支付记录
                if ("TRADE_SUCCESS".equals(response.getTradeStatus()) || "TRADE_FINISHED".equals(response.getTradeStatus())) {
                    PayRecord payRecord = payRecordService.lambdaQuery()
                            .eq(PayRecord::getOutTradeNo, outTradeNo)
                            .one();
                    
                    if (payRecord != null && payRecord.getPayStatus() != 1) {
                        payRecord.setPayStatus(1); // 支付成功
                        payRecord.setTradeNo(response.getTradeNo());
                        payRecord.setPayTime(LocalDateTime.now());
                        payRecordService.updateById(payRecord);
                    }
                }
            } else {
                result.put("success", false);
                result.put("errorMsg", response.getSubMsg());
            }
        } catch (Exception e) {
            log.error("查询支付宝订单失败", e);
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> queryWxpayOrder(String outTradeNo) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 构建查询参数
            SortedMap<String, String> params = new TreeMap<>();
            params.put("appid", wxPayConfig.getAppId());
            params.put("mch_id", wxPayConfig.getMchId());
            params.put("out_trade_no", outTradeNo);
            params.put("nonce_str", generateNonceStr());
            
            // 生成签名
            String sign = generateWxSign(params, wxPayConfig.getMchKey());
            params.put("sign", sign);
            
            // 转换为XML
            String xmlData = mapToXml(params);
            
            // 发送请求到微信支付API
            // 这里需要使用HTTP客户端发送请求，简化起见，假设已经获取到了返回结果
            String responseXml = ""; // 实际项目中需要发送HTTP请求获取结果
            
            // 解析返回结果
            Map<String, String> responseMap = xmlToMap(responseXml);
            
            if ("SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"))) {
                result.put("success", true);
                result.put("tradeState", responseMap.get("trade_state"));
                result.put("tradeNo", responseMap.get("transaction_id"));
                
                // 如果支付成功，更新支付记录
                if ("SUCCESS".equals(responseMap.get("trade_state"))) {
                    PayRecord payRecord = payRecordService.lambdaQuery()
                            .eq(PayRecord::getOutTradeNo, outTradeNo)
                            .one();
                    
                    if (payRecord != null && payRecord.getPayStatus() != 1) {
                        payRecord.setPayStatus(1); // 支付成功
                        payRecord.setTradeNo(responseMap.get("transaction_id"));
                        payRecord.setPayTime(LocalDateTime.now());
                        payRecordService.updateById(payRecord);
                    }
                }
            } else {
                result.put("success", false);
                result.put("errorMsg", responseMap.get("return_msg"));
            }
        } catch (Exception e) {
            log.error("查询微信支付订单失败", e);
            result.put("success", false);
            result.put("errorMsg", e.getMessage());
        }
        
        return result;
    }
    
    // 生成随机字符串
    private String generateNonceStr() {
        return Long.toString(System.currentTimeMillis());
    }
    
    // 生成微信支付签名
    private String generateWxSign(Map<String, String> params, String key) {
        // 实际项目中需要按照微信支付签名规则生成签名
        return "签名结果";
    }
    
    // Map转XML
    private String mapToXml(Map<String, String> params) {
        // 实际项目中需要实现Map转XML的逻辑
        return "<xml></xml>";
    }
    
    // XML转Map
    private Map<String, String> xmlToMap(String xmlData) {
        // 实际项目中需要实现XML转Map的逻辑
        return new HashMap<>();
    }
} 