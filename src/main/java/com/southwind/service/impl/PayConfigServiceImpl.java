package com.southwind.service.impl;

import com.southwind.entity.AlipayConfig;
import com.southwind.entity.WxpayConfig;
import com.southwind.mapper.AlipayConfigMapper;
import com.southwind.mapper.WxpayConfigMapper;
import com.southwind.service.PayConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 支付配置服务实现类
 */
@Service
public class PayConfigServiceImpl implements PayConfigService {

    @Autowired
    private AlipayConfigMapper alipayConfigMapper;
    
    @Autowired
    private WxpayConfigMapper wxpayConfigMapper;
    
    @Override
    public AlipayConfig getAlipayConfig() {
        return alipayConfigMapper.selectById(1);
    }
    
    @Override
    public void updateAlipayConfig(AlipayConfig config) {
        config.setId(1); // 确保ID为1
        alipayConfigMapper.updateById(config);
    }
    
    @Override
    public WxpayConfig getWxpayConfig() {
        return wxpayConfigMapper.selectById(1);
    }
    
    @Override
    public void updateWxpayConfig(WxpayConfig config) {
        config.setId(1); // 确保ID为1
        wxpayConfigMapper.updateById(config);
    }
} 