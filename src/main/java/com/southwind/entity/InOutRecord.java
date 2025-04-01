package com.southwind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 出入场记录表
 * </p>
 *
 * @author admin
 * @since 2023-07-26
 */
@Data
@TableName("in_out_record")
public class InOutRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "in_out_record_id", type = IdType.AUTO)
    private Integer inOutRecordId;

    /**
     * 停车场ID
     */
    private Integer parkId;

    /**
     * 车牌号
     */
    private String number;

    /**
     * 入场时间
     */
    private Date inTime;

    /**
     * 出场时间
     */
    private Date outTime;

    /**
     * 入场图片
     */
    private String inPic;

    /**
     * 出场图片
     */
    private String outPic;

    /**
     * 车辆类型：1-临时车辆，2-固定车辆
     */
    private Integer payType;

    /**
     * 停车费用
     */
    private Double money;
}
