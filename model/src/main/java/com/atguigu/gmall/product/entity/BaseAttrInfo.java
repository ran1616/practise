package com.atguigu.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 属性表
 * @TableName base_attr_info  234324324
 */
@TableName(value ="base_attr_info")
@Data
@ApiModel(description = "平台属性实体类")
public class BaseAttrInfo implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "编号")
    private Long id;

    /**
     * 属性名称
     */
    @ApiModelProperty(value = "属性名称")
    private String attrName;

    /**
     * 分类id
     */
    @ApiModelProperty(value = "分类id")
    private Long categoryId;

    /**
     * 分类层级
     */
    @ApiModelProperty(value = "分类级别")
    private Integer categoryLevel;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "平台属性值")
    @TableField(exist = false)      // 在保存BaseAttrInfo对象到数据库表中的时候，忽略此字段
    private List<BaseAttrValue> attrValueList ;

}