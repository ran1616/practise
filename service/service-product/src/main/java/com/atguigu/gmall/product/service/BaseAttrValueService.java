package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_value(属性值表)】的数据库操作Service
* @createDate 2023-07-10 16:46:04
*/
public interface BaseAttrValueService extends IService<BaseAttrValue> {

    public abstract List<BaseAttrValue> findByAttrId(Long attrId);
}
