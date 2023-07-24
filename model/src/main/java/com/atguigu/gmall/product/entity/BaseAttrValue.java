package com.atguigu.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 属性值表
 * @TableName base_attr_value
 */
@TableName(value ="base_attr_value")
@Data
@ApiModel(value = "平台属性值实体类")
public class BaseAttrValue implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(name = "编号")
    private Long id;

    /**
     * 属性值名称
     */
    @ApiModelProperty(name = "属性值名称")
    private String valueName;

    /**
     * 属性id
     */
    @ApiModelProperty(name = "属性id")
    private Long attrId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}