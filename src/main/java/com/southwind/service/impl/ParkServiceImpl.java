package com.southwind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.InOutRecord;
import com.southwind.entity.Park;
import com.southwind.form.ParkListForm;
import com.southwind.mapper.InOutRecordMapper;
import com.southwind.mapper.ParkMapper;
import com.southwind.mapper.PropertyMapper;
import com.southwind.service.ParkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.southwind.vo.PageVO;
import com.southwind.vo.ParkVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
 * @since 2023-12-12
 */
@Service
public class ParkServiceImpl extends ServiceImpl<ParkMapper, Park> implements ParkService {

    @Autowired
    private ParkMapper parkMapper;
    @Autowired
    private PropertyMapper propertyMapper;
    @Autowired
    private InOutRecordMapper inOutRecordMapper;

    @Override
    public PageVO parkList(ParkListForm parkListForm) {
        Page<Park> parkPage = new Page<>(parkListForm.getPage(), parkListForm.getLimit());
        QueryWrapper<Park> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(parkListForm.getPropertyId() != null,"property_id", parkListForm.getPropertyId())
                .like(StringUtils.isNotBlank(parkListForm.getParkName()), "park_name", parkListForm.getParkName());
        Page<Park> selectPage = this.parkMapper.selectPage(parkPage, queryWrapper);
        PageVO pageVO = new PageVO();
        List<ParkVO> list = new ArrayList<>();
        for (Park record : selectPage.getRecords()) {
            ParkVO vo = new ParkVO();
            BeanUtils.copyProperties(record, vo);
            vo.setPropertyName(this.propertyMapper.getNameById(record.getPropertyId()));
            list.add(vo);
        }
        pageVO.setList(list);
        pageVO.setTotalPage(selectPage.getPages());
        pageVO.setCurrPage(selectPage.getCurrent());
        pageVO.setPageSize(selectPage.getSize());
        pageVO.setTotalCount(selectPage.getTotal());
        return pageVO;
    }

    @Override
    public List<Map<String, Object>> getParkLocations() {
        // 查询所有停车场信息
        List<Park> parks = this.list();
        List<Map<String, Object>> result = new ArrayList<>();

        // 为每个停车场添加位置信息
        for (Park park : parks) {
            Map<String, Object> parkInfo = new HashMap<>();
            parkInfo.put("parkId", park.getParkId());
            parkInfo.put("parkName", park.getParkName());
            
            // 处理totalSpaces可能为空的情况
            Integer totalSpaces = park.getTotalSpaces();
            if (totalSpaces == null) {
                totalSpaces = 100; // 默认值
            }
            parkInfo.put("totalSpaces", totalSpaces);

            // 查询当前在场车辆数
            QueryWrapper<InOutRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("park_id", park.getParkId());
            queryWrapper.isNull("out_time"); // 未出场的车辆
            int occupiedSpaces = inOutRecordMapper.selectCount(queryWrapper);

            // 计算剩余车位和占用率
            int availableSpaces = Math.max(0, totalSpaces - occupiedSpaces);
            int occupancyRate = totalSpaces > 0 ? (occupiedSpaces * 100 / totalSpaces) : 0;

            parkInfo.put("occupiedSpaces", occupiedSpaces);
            parkInfo.put("availableSpaces", availableSpaces);
            parkInfo.put("occupancyRate", occupancyRate);

            // 添加经纬度信息 - 这里使用模拟数据，实际应从数据库获取
            // 中国主要城市的经纬度范围
            double minLng = 104.0;  // 最小经度
            double maxLng = 120.0;  // 最大经度
            double minLat = 30.0;   // 最小纬度
            double maxLat = 40.0;   // 最大纬度

            // 生成随机经纬度
            Random random = new Random(park.getParkId()); // 使用parkId作为种子，确保每次生成相同的随机数
            double longitude = minLng + (maxLng - minLng) * random.nextDouble();
            double latitude = minLat + (maxLat - minLat) * random.nextDouble();

            parkInfo.put("longitude", longitude);
            parkInfo.put("latitude", latitude);

            result.add(parkInfo);
        }

        return result;
    }
}
