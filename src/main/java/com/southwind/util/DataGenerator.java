package com.southwind.util;

import com.southwind.entity.InOutRecord;
import com.southwind.entity.PayRecord;
import com.southwind.entity.Park;
import com.southwind.mapper.InOutRecordMapper;
import com.southwind.mapper.PayRecordMapper;
import com.southwind.mapper.ParkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 数据生成工具类#   http://localhost:8080/data/generate?count=1000
 */
@Component
public class DataGenerator {

    @Autowired
    private InOutRecordMapper inOutRecordMapper;

    @Autowired
    private PayRecordMapper payRecordMapper;

    @Autowired
    private ParkMapper parkMapper;

    // 车牌前缀
    private static final String[] LICENSE_PREFIXES = {"京", "津", "冀", "晋", "蒙", "辽", "吉", "黑", "沪", "苏",
                                                     "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "桂"};

    // 车牌字母
    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ";

    // 支付方式
    private static final String[] PAY_METHODS = {"alipay", "wxpay", "cash"};

    /**
     * 生成并保存模拟数据
     */
    @Transactional
    public void generateAndSaveData(int count) {
        Random random = new Random();

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 30天前的时间
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        // 获取所有停车场
        List<Park> allParks = parkMapper.selectList(null);
        if (allParks == null || allParks.isEmpty()) {
            throw new RuntimeException("没有找到停车场数据，请先添加停车场");
        }

        List<InOutRecord> inOutRecords = new ArrayList<>();
        List<PayRecord> payRecords = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            // 生成随机车牌号
            String licensePlate = generateRandomLicensePlate(random);

            // 随机选择一个停车场
            Park randomPark = allParks.get(random.nextInt(allParks.size()));
            Integer parkId = randomPark.getParkId();

            // 随机生成入场时间（在过去30天内）
            LocalDateTime inTime = randomDateTime(thirtyDaysAgo, now);

            // 随机生成停车时长（1小时到24小时之间）
            long parkingHours = random.nextInt(23) + 1;
            LocalDateTime outTime = inTime.plusHours(parkingHours);

            // 如果出场时间超过当前时间，则设置为null（表示车辆仍在停车场内）
            boolean isStillParking = outTime.isAfter(now);
            if (isStillParking) {
                outTime = null;
            }

            // 随机生成支付方式
            String payMethod = PAY_METHODS[random.nextInt(PAY_METHODS.length)];

            // 随机生成是否为固定车辆
            boolean isFixedVehicle = random.nextInt(10) < 2; // 20%的概率是固定车辆
            int payType = isFixedVehicle ? 2 : 1; // 1-临时车辆，2-固定车辆

            // 创建入场出场记录
            InOutRecord record = new InOutRecord();
            record.setNumber(licensePlate);
            record.setParkId(parkId);
            // 将 LocalDateTime 转换为 Date
            record.setInTime(Date.from(inTime.atZone(ZoneId.systemDefault()).toInstant()));
            record.setPayType(payType);

            // 如果已经出场，设置出场时间和停车费用
            if (outTime != null) {
                record.setOutTime(Date.from(outTime.atZone(ZoneId.systemDefault()).toInstant()));

                // 计算停车时长（小时）
                long durationHours = ChronoUnit.HOURS.between(inTime, outTime);
                record.setDuration((int) durationHours);

                // 计算停车费用
                BigDecimal amount;
                if (isFixedVehicle) {
                    // 固定车辆免费
                    amount = BigDecimal.ZERO;
                } else {
                    // 临时车辆按小时收费，每小时5-15元
                    int hourlyRate = random.nextInt(11) + 5;
                    amount = new BigDecimal(hourlyRate * durationHours)
                            .setScale(2, RoundingMode.HALF_UP);
                }
                record.setAmount(amount);

                // 创建支付记录
                PayRecord payRecord = new PayRecord();
                payRecord.setParkId(parkId);
                payRecord.setPropertyId(parkId); // 假设物业ID与停车场ID相同
                payRecord.setNumber(licensePlate);
                payRecord.setAmount(amount);
                payRecord.setPayMethod(payMethod);
                payRecord.setPayType(payType);
                payRecord.setPayTime(outTime);
                payRecord.setOutTradeNo("PARK" + System.currentTimeMillis() + i);
                payRecord.setPayStatus(1); // 已支付
                payRecord.setCreateTime(new Date()); // 创建时间使用当前时间

                payRecords.add(payRecord);
            }

            inOutRecords.add(record);
        }

        // 批量保存入场出场记录
        for (InOutRecord record : inOutRecords) {
            inOutRecordMapper.insert(record);
        }

        // 批量保存支付记录
        for (PayRecord record : payRecords) {
            payRecordMapper.insert(record);
        }

        System.out.println("成功生成 " + inOutRecords.size() + " 条入场出场记录");
        System.out.println("成功生成 " + payRecords.size() + " 条支付记录");
    }

    /**
     * 生成随机日期时间
     */
    private LocalDateTime randomDateTime(LocalDateTime start, LocalDateTime end) {
        long startEpochSecond = start.atZone(ZoneId.systemDefault()).toEpochSecond();
        long endEpochSecond = end.atZone(ZoneId.systemDefault()).toEpochSecond();
        long randomEpochSecond = ThreadLocalRandom.current().nextLong(startEpochSecond, endEpochSecond);

        return LocalDateTime.ofInstant(
            java.time.Instant.ofEpochSecond(randomEpochSecond),
            ZoneId.systemDefault()
        );
    }

    /**
     * 生成随机车牌号
     */
    private String generateRandomLicensePlate(Random random) {
        StringBuilder sb = new StringBuilder();

        // 添加省份简称
        sb.append(LICENSE_PREFIXES[random.nextInt(LICENSE_PREFIXES.length)]);

        // 添加字母
        sb.append(LETTERS.charAt(random.nextInt(LETTERS.length())));

        // 添加5位数字和字母组合
        for (int i = 0; i < 5; i++) {
            if (random.nextBoolean()) {
                // 添加数字
                sb.append(random.nextInt(10));
            } else {
                // 添加字母
                sb.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
            }
        }

        return sb.toString();
    }
}
