package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_info(属性表)】的数据库操作Mapper
* @createDate 2023-07-10 16:46:04
* @Entity com.atguigu.gmall.product.entity.BaseAttrInfo
*/
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    public abstract List<BaseAttrInfo> findBaseAttrInfo(@Param(value = "c1Id") Long c1Id, @Param(value = "c2Id")Long c2Id, @Param(value = "c3Id")Long c3Id);

}




