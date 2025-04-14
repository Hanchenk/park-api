-- 创建支付宝配置表
CREATE TABLE IF NOT EXISTS `alipay_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(100) DEFAULT NULL COMMENT '应用ID',
  `private_key` text COMMENT '商户私钥',
  `alipay_public_key` text COMMENT '支付宝公钥',
  `notify_url` varchar(255) DEFAULT NULL COMMENT '回调地址',
  `gateway_url` varchar(255) DEFAULT 'https://openapi.alipay.com/gateway.do' COMMENT '网关地址',
  `sign_type` varchar(20) DEFAULT 'RSA2' COMMENT '签名方式',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付宝配置表';

-- 创建微信支付配置表
CREATE TABLE IF NOT EXISTS `wxpay_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(100) DEFAULT NULL COMMENT '应用ID',
  `mch_id` varchar(100) DEFAULT NULL COMMENT '商户号',
  `mch_key` varchar(100) DEFAULT NULL COMMENT '商户密钥',
  `notify_url` varchar(255) DEFAULT NULL COMMENT '回调地址',
  `trade_type` varchar(20) DEFAULT 'JSAPI' COMMENT '交易类型',
  `cert_path` varchar(255) DEFAULT NULL COMMENT '证书路径',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信支付配置表';

-- 插入初始数据
INSERT INTO `alipay_config` (`id`, `app_id`, `private_key`, `alipay_public_key`, `notify_url`, `gateway_url`, `sign_type`, `enabled`)
VALUES (1, '', '', '', 'http://localhost:8080/api/pay/notify/alipay', 'https://openapi.alipay.com/gateway.do', 'RSA2', 1);

INSERT INTO `wxpay_config` (`id`, `app_id`, `mch_id`, `mch_key`, `notify_url`, `trade_type`, `cert_path`, `enabled`)
VALUES (1, '', '', '', 'http://localhost:8080/api/pay/notify/wxpay', 'JSAPI', '', 1); 