package com.southwind.service;

import com.southwind.entity.Park;
import com.baomidou.mybatisplus.extension.service.IService;
import com.southwind.form.ParkListForm;
import com.southwind.vo.PageVO;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2023-12-12
 */
public interface ParkService extends IService<Park> {
    public PageVO parkList(ParkListForm parkListForm);

    /**
     * 获取停车场位置信息
     * @return 停车场位置信息列表
     */
    List<Map<String, Object>> getParkLocations();
}
