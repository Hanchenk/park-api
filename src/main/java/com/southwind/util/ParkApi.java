package com.southwind.util;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.LicensePlateOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.LicensePlateOCRResponse;

public class ParkApi {

    public static String getNumber(String fileBase64){
        try{
            // 检查输入参数
            if (fileBase64 == null || fileBase64.isEmpty()) {
                System.out.println("车牌图片数据为空");
                return null;
            }
            
            // 如果base64字符串包含前缀，需要去除
            if (fileBase64.contains(",")) {
                fileBase64 = fileBase64.substring(fileBase64.indexOf(",") + 1);
            }
            
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey
            Credential cred = new Credential(
                    "AKIDiIuR1hGE3f3HK9lCSVsCXfZKTVKzXv7I",
                    "5ILBpIckjZGqETXQ9g7SbXGpCMRO9i4D");
            // 实例化一个http选项
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("ocr.tencentcloudapi.com");
            // 实例化一个client选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象
            OcrClient client = new OcrClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            LicensePlateOCRRequest req = new LicensePlateOCRRequest();
            // 传入车牌数据
            req.setImageBase64(fileBase64);
            // 返回的resp是一个LicensePlateOCRResponse的实例，与请求对象对应
            LicensePlateOCRResponse resp = client.LicensePlateOCR(req);
            // 获取json格式的字符串回包
            String response = LicensePlateOCRResponse.toJsonString(resp);
            System.out.println("车牌识别API响应: " + response);
            
            // 解析json数据
            try {
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                if (jsonObject.has("Number")) {
                    return jsonObject.get("Number").getAsString();
                } else if (jsonObject.has("Response")) {
                    JsonObject responseObj = jsonObject.getAsJsonObject("Response");
                    if (responseObj.has("Number")) {
                        return responseObj.get("Number").getAsString();
                    }
                }
            } catch (Exception e) {
                System.out.println("JSON解析异常: " + e.getMessage());
            }
            
            // 尝试使用Map解析
            Map<String, Object> map = new Gson().fromJson(response, new TypeToken<Map<String, Object>>(){}.getType());
            if (map.containsKey("Number")) {
                return (String) map.get("Number");
            } else if (map.containsKey("Response")) {
                Map<String, Object> responseMap = (Map<String, Object>) map.get("Response");
                if (responseMap.containsKey("Number")) {
                    return (String) responseMap.get("Number");
                }
            }
            
            System.out.println("未能从API响应中提取车牌号");
            return null;
        } catch (TencentCloudSDKException e) {
            System.out.println("车牌识别API异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("车牌识别过程中发生未知异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
