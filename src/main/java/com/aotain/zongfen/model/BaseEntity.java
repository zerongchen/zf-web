package com.aotain.zongfen.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 基础实体类
 *
 * @author daiyh@aotain.com
 * @date 2018/02/26
 */
@Getter
@Setter
public class BaseEntity {

    /**
     * 创建人
     */
    private String createOper;

    /**
     * 修改人
     */
    private String modifyOper;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 策略号
     */
    private Long messageNo;

    /**
     * 策略序列号
     */
    private Long messageSequenceNo;

    /**
     * 设备类型
     */
    private Integer probeType;

    /**
     * 策略类型
     */
    private Integer messageType;

}
