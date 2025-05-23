package com.southwind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 停车场实体类
 * </p>
 *
 * @author admin
 * @since 2023-12-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("park")
public class Park implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "park_id", type = IdType.AUTO)
    private Integer parkId;

    private Integer propertyId;

    private String parkName;

    private Integer parkCount;

    private Integer monthlyPrice;

    private Integer freeDuration;

    private Integer chargeUnit;

    private Integer chargePrice;

    private Integer maxCharge;

    private Float lng;

    private Float lat;

    /**
     * 总车位数
     */
    private Integer totalSpaces;

    public Integer getTotalSpaces() {
        return totalSpaces;
    }

    public void setTotalSpaces(Integer totalSpaces) {
        this.totalSpaces = totalSpaces;
    }
}
