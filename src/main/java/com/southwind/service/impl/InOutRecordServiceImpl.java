package com.southwind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.southwind.entity.InOutRecord;
import com.southwind.entity.Park;
import com.southwind.form.InOutQueryForm;
import com.southwind.mapper.InOutRecordMapper;
import com.southwind.mapper.ParkMapper;
import com.southwind.service.InOutRecordService;
import com.southwind.vo.ChartVO;
import com.southwind.vo.InOutRecordVO;
import com.southwind.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author admin
 * @since 2023-07-26
 */
@Service
public class InOutRecordServiceImpl extends ServiceImpl<InOutRecordMapper, InOutRecord> implements InOutRecordService {

    @Autowired
    private InOutRecordMapper inOutRecordMapper;
    @Autowired
    private ParkMapper parkMapper;

    @Override
    public Map chart() {
        List<ChartVO> list = this.inOutRecordMapper.chart();
        String[] names = new String[list.size()];
        Double[] values = new Double[list.size()];
        for(int i = 0; i < list.size(); i++){
            names[i] = list.get(i).getName();
            values[i] = Double.valueOf(list.get(i).getValue());
        }
        Map map = new HashMap();
        map.put("names", names);
        map.put("values", values);
        return map;
    }

    @Override
    public PageVO inOutRecordList(InOutQueryForm inOutQueryForm) {
        // 防止空指针异常
        if (inOutQueryForm == null) {
            inOutQueryForm = new InOutQueryForm();
        }

        // 设置默认值
        Integer page = inOutQueryForm.getPage() != null ? inOutQueryForm.getPage() : 1;
        Integer limit = inOutQueryForm.getLimit() != null ? inOutQueryForm.getLimit() : 10;

        Page<InOutRecord> pageParam = new Page<>(page, limit);
        QueryWrapper<InOutRecord> queryWrapper = new QueryWrapper<>();

        // 添加查询条件
        if (!StringUtils.isEmpty(inOutQueryForm.getNumber())) {
            queryWrapper.like("number", inOutQueryForm.getNumber());
        }

        // 添加日期范围查询
        if (!StringUtils.isEmpty(inOutQueryForm.getStartDate()) && !StringUtils.isEmpty(inOutQueryForm.getEndDate())) {
            queryWrapper.between("in_time", inOutQueryForm.getStartDate(), inOutQueryForm.getEndDate());
        }

        // 按入场时间降序排序
        queryWrapper.orderByDesc("in_time");

        // 执行分页查询
        Page<InOutRecord> resultPage = this.page(pageParam, queryWrapper);

        // 转换为VO对象
        List<InOutRecordVO> voList = new ArrayList<>();
        for (InOutRecord record : resultPage.getRecords()) {
            InOutRecordVO vo = new InOutRecordVO();
            vo.setInOutRecordId(record.getInOutRecordId());
            vo.setNumber(record.getNumber());
            vo.setInTime(record.getInTime());
            vo.setOutTime(record.getOutTime());
            vo.setInPic(record.getInPic());
            vo.setOutPic(record.getOutPic());

            // 获取停车场名称
            if (record.getParkId() != null) {
                Park park = parkMapper.selectById(record.getParkId());
                if (park != null) {
                    vo.setParkName(park.getParkName());
                }
            }

            // 设置支付类型
            if (record.getPayType() != null) {
                vo.setPayType(record.getPayType() == 1 ? "临时车辆" : "固定车辆");
            }

            voList.add(vo);
        }

        // 构建分页VO
        PageVO pageVO = new PageVO();
        pageVO.setList(voList);
        pageVO.setTotalCount(resultPage.getTotal());
        pageVO.setCurrPage(resultPage.getCurrent());
        pageVO.setPageSize(resultPage.getSize());
        pageVO.setTotalPage(resultPage.getPages());

        return pageVO;
    }

    @Override
    public Map<String, Object> getDailyTrafficTrend() {
        // 初始化24小时的数据
        int[] inData = new int[24];
        int[] outData = new int[24];
        
        try {
            // 获取今天的日期
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new Date());
            
            // 查询今日入场记录
            QueryWrapper<InOutRecord> inQueryWrapper = new QueryWrapper<>();
            inQueryWrapper.apply("DATE_FORMAT(in_time, '%Y-%m-%d') = {0}", today);
            inQueryWrapper.select("HOUR(in_time) as hour", "COUNT(*) as count");
            inQueryWrapper.groupBy("HOUR(in_time)");
            
            List<Map<String, Object>> inResults = this.baseMapper.selectMaps(inQueryWrapper);
            for (Map<String, Object> result : inResults) {
                int hour = ((Number) result.get("hour")).intValue();
                int count = ((Number) result.get("count")).intValue();
                if (hour >= 0 && hour < 24) {
                    inData[hour] = count;
                }
            }
            
            // 查询今日出场记录
            QueryWrapper<InOutRecord> outQueryWrapper = new QueryWrapper<>();
            outQueryWrapper.apply("DATE_FORMAT(out_time, '%Y-%m-%d') = {0}", today);
            outQueryWrapper.isNotNull("out_time");
            outQueryWrapper.select("HOUR(out_time) as hour", "COUNT(*) as count");
            outQueryWrapper.groupBy("HOUR(out_time)");
            
            List<Map<String, Object>> outResults = this.baseMapper.selectMaps(outQueryWrapper);
            for (Map<String, Object> result : outResults) {
                int hour = ((Number) result.get("hour")).intValue();
                int count = ((Number) result.get("count")).intValue();
                if (hour >= 0 && hour < 24) {
                    outData[hour] = count;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取车流量趋势数据异常: " + e.getMessage());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("inData", inData);
        result.put("outData", outData);
        
        return result;
    }

    /**
     * 生成模拟数据，用于测试
     */
    private Map<String, Object> generateMockData() {
        int[] inData = new int[24];
        int[] outData = new int[24];
        
        // 生成随机数据
        Random random = new Random();
        for (int i = 0; i < 24; i++) {
            inData[i] = random.nextInt(10); // 0-9之间的随机数
            outData[i] = random.nextInt(8); // 0-7之间的随机数
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("inData", inData);
        result.put("outData", outData);
        
        return result;
    }
}
