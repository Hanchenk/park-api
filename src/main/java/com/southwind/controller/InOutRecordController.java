package com.southwind.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.southwind.annotation.LogAnnotation;
import com.southwind.configuration.ApiConfiguration;
import com.southwind.entity.*;
import com.southwind.form.InOutParkForm;
import com.southwind.form.InOutQueryForm;
import com.southwind.mapper.CarMapper;
import com.southwind.mapper.InOutRecordMapper;
import com.southwind.mapper.ParkMapper;
import com.southwind.service.*;
import com.southwind.util.Base64Util;
import com.southwind.util.ParkApi;
import com.southwind.util.ParkUtil;
import com.southwind.util.Result;
import com.southwind.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2023-07-26
 */
@RestController
@RequestMapping("/sys/inOut")
public class InOutRecordController {

    @Autowired
    private InOutRecordService inOutRecordService;
    @Value("${upload.numberUrl}")
    private String numberUrl;
    @Value("${upload.urlPrefix}")
    private String urlPrefix;
    @Autowired
    private InOutRecordMapper inOutRecordMapper;
    @Autowired
    private ParkService parkService;
    @Autowired
    private PayRecordService payRecordService;
    @Autowired
    private CarMapper carMapper;
    @Autowired
    private ParkMapper parkMapper;

    @GetMapping("/chart")
    public Result chart(){
        Map map = this.inOutRecordService.chart();
        return Result.ok().put("data", map);
    }

    @GetMapping("/parkList")
    public Result parkList(){
        List<Park> list = this.parkService.list();
        if(list == null) return Result.error("没有停车场数据");
        return Result.ok().put("data", list);
    }

    @LogAnnotation("车牌识别")
    @PostMapping("/add")
    public Result add(@RequestBody InOutParkForm inOutParkForm){
        String fileBase64 = inOutParkForm.getFileBase64();
        //调用腾讯AI接口
        String number = null;

        try {
            // 首先尝试使用腾讯云API识别车牌
            System.out.println("尝试使用腾讯云API识别车牌...");
            number = ParkApi.getNumber(fileBase64);

            // 如果腾讯云API识别失败，使用模拟方法
            if(number == null || number.isEmpty()){
                System.out.println("腾讯云API识别失败，使用模拟方法...");
                String faceBase = fileBase64.substring(0, Math.min(fileBase64.length(), 60));
                number = ParkUtil.getNumber(faceBase);
            }

            System.out.println("最终识别到的车牌号: " + number);

            if(number == null || number.isEmpty()){
                return Result.ok().put("status", "fail").put("data", "车牌识别失败");
            }

            //保存图片
            String newFileName = UUID.randomUUID()+"." + inOutParkForm.getExtName();
            String fileName = numberUrl + newFileName;
            Base64Util.decoderBase64File(fileBase64, fileName);
            String basePath = urlPrefix + "park/upload/number/" + newFileName;

            //入场出场
            InOutRecord inOutRecord = new InOutRecord();
            inOutRecord.setParkId(inOutParkForm.getParkId());
            inOutRecord.setNumber(number);

            // 打印调试信息
            System.out.println("准备保存记录: parkId=" + inOutRecord.getParkId() + ", number=" + inOutRecord.getNumber());

            //查找系统中是否有该车辆的出入场信息
            InOutRecord existingRecord = this.inOutRecordMapper.getInOutRecord(inOutRecord);
            Park park = this.parkService.getById(inOutRecord.getParkId());
            Car car = this.carMapper.getByNumber(number);

            if (park == null) {
                System.out.println("错误: 未找到停车场信息，parkId=" + inOutRecord.getParkId());
                return Result.ok().put("status", "fail").put("data", "未找到停车场信息");
            }

            System.out.println("停车场信息: " + park.getParkName());
            System.out.println("现有记录: " + (existingRecord == null ? "无" : "有"));

            //进入停车场
            if(existingRecord == null) {
                // 新车入场
                inOutRecord.setInTime(new Date());
                inOutRecord.setInPic(basePath);
                inOutRecord.setPayType(car == null ? 1 : 2); // 1-临时车辆，2-固定车辆

                try {
                    int insertResult = this.inOutRecordMapper.insert(inOutRecord);
                    System.out.println("插入结果: " + insertResult + ", 记录ID: " + inOutRecord.getInOutRecordId());

                    if (insertResult > 0) {
                        return Result.ok()
                                .put("status", "success")
                                .put("data", "【"+ number + "】进入"+"【"+ park.getParkName() +"】")
                                .put("recordId", inOutRecord.getInOutRecordId());
                    } else {
                        return Result.ok().put("status", "fail").put("data", "保存入场记录失败");
                    }
                } catch (Exception e) {
                    System.out.println("保存入场记录异常: " + e.getMessage());
                    e.printStackTrace();
                    return Result.ok().put("status", "fail").put("data", "保存入场记录异常: " + e.getMessage());
                }
            } else {
                // 车辆出场
                existingRecord.setOutTime(new Date());
                existingRecord.setOutPic(basePath);

                    // 计算停车费用
                    Park park1 = this.parkService.getById(existingRecord.getParkId());
                    Map<String, Integer> feeMap = ParkUtil.parkPay(
                        existingRecord.getInTime(),
                        existingRecord.getOutTime(),
                        park1.getFreeDuration(),
                        park1.getChargePrice(),
                        park1.getMaxCharge()
                    );

                    Integer hours = feeMap != null ? feeMap.get("hour") : 0;
                    Integer amount = feeMap != null ? feeMap.get("amount") : 0;

                // 将费用信息保存到 in_out_record 表中
                existingRecord.setAmount(new BigDecimal(amount));
                existingRecord.setDuration(hours);

                try {
                    int updateResult = this.inOutRecordMapper.updateById(existingRecord);
                    System.out.println("更新结果: " + updateResult);

                    if (updateResult <= 0) {
                        return Result.ok().put("status", "fail").put("data", "更新出场记录失败");
                    }

                    // 生成订单号
                    String outTradeNo = "PARK" + System.currentTimeMillis() + existingRecord.getInOutRecordId();

                    // 保存支付记录
                    PayRecord payRecord = new PayRecord();
                    payRecord.setPropertyId(park.getPropertyId());
                    payRecord.setParkId(park.getParkId());
                    payRecord.setNumber(number);
                    payRecord.setOutTradeNo(outTradeNo);
                    payRecord.setPayStatus(0); // 待支付
                    payRecord.setAmount(new BigDecimal(amount)); // 使用 BigDecimal 类型
                    payRecord.setPayType(existingRecord.getPayType());
                    payRecord.setCreateTime(new Date());

                    String result;
                    if(car == null) {
                        // 临时车
                        payRecord.setPayType(1);
                        result = "【临时车】"+ number +"离开"+"【"+ park.getParkName() +"】停车"+hours+"小时，需缴费"+amount+"元";
                    } else {
                        // 包月车
                        payRecord.setPayType(2);
                        Date effectTime = car.getEffectTime();
                        if(effectTime != null && effectTime.after(new Date())) {
                            payRecord.setAmount(new BigDecimal(0));
                            result = "【包月车】"+ number +"离开"+"【"+ park.getParkName() +"】停车"+hours+"小时，无需缴费";
                        } else {
                            payRecord.setAmount(new BigDecimal(amount));
                            result = "【包月车】"+ number +"离开"+"【"+ park.getParkName() +"】停车"+hours+"小时，需缴费"+amount+"元";
                        }
                    }

                    // 在保存支付记录之前添加 0 元支付的处理
                    if(payRecord.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                        payRecord.setPayStatus(1); // 已支付
                        payRecord.setTradeNo("/"); // 0元支付无交易号
                        payRecord.setPayMethod("free"); // 免费
                        payRecord.setPayTime(LocalDateTime.now()); // 设置支付时间
                    }

                    boolean saveResult = this.payRecordService.save(payRecord);

                    return Result.ok()
                            .put("status", "success")
                            .put("data", result)
                            .put("recordId", existingRecord.getInOutRecordId())
                            .put("payRecordId", payRecord.getPayRecordId())
                            .put("outTradeNo", outTradeNo)
                            .put("amount", amount);
                } catch (Exception e) {
                    System.out.println("处理出场记录异常: " + e.getMessage());
                    e.printStackTrace();
                    return Result.ok().put("status", "fail").put("data", "处理出场记录异常: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("车牌识别处理异常: " + e.getMessage());
            e.printStackTrace();
            return Result.ok().put("status", "fail").put("data", "处理过程中发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result list(InOutQueryForm inOutQueryForm){
        PageVO pageVO = this.inOutRecordService.inOutRecordList(inOutQueryForm);
        Map map = new HashMap();
        map.put("pageList",pageVO);
        return Result.ok().put("data", map);
    }

    /**
     * 获取最新停车记录
     */
    @GetMapping("/latest")
    public Result getLatestRecords() {
        try {
            // 获取最新的10条记录
            QueryWrapper<InOutRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("in_time");
            queryWrapper.last("LIMIT 10");
            
            List<InOutRecord> records = inOutRecordService.list(queryWrapper);

            // 防止空指针异常
            if (records == null) {
                return Result.ok().put("data", new ArrayList<>());
            }

            List<Map<String, Object>> resultList = new ArrayList<>();
            
            for (InOutRecord record : records) {
                Map<String, Object> map = new HashMap<>();
                map.put("inOutRecordId", record.getInOutRecordId());
                map.put("parkId", record.getParkId());
                
                // 获取停车场名称
                if (record.getParkId() != null) {
                    String parkName = parkMapper.getNameById(record.getParkId());
                    map.put("parkName", parkName != null ? parkName : "未知停车场");
                } else {
                    map.put("parkName", "未知停车场");
                }

                map.put("number", record.getNumber());
                map.put("inTime", record.getInTime());
                map.put("outTime", record.getOutTime());
                map.put("inPic", record.getInPic());
                map.put("outPic", record.getOutPic());
                map.put("payType", record.getPayType());
                map.put("amount", record.getAmount());
                map.put("duration", record.getDuration());
                
                resultList.add(map);
            }
            
            return Result.ok().put("data", resultList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取最新停车记录失败: " + e.getMessage());
        }
    }
}

