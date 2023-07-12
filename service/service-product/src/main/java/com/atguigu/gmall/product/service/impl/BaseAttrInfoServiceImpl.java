package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.entity.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.sun.org.apache.xerces.internal.xs.datatypes.ByteList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2023-07-10 16:46:04
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo> implements BaseAttrInfoService{

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper  ;

    @Autowired
    private BaseAttrValueService baseAttrValueService ;

    @Override
    public List<BaseAttrInfo> findBaseAttrInfo(Long c1Id, Long c2Id, Long c3Id) {
        return baseAttrInfoMapper.findBaseAttrInfo(c1Id , c2Id , c3Id);
    }

    @Transactional   // 事务控制
    @Override
    public void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo) {

        Long baseAttrInfoId = baseAttrInfo.getId();
        if(baseAttrInfoId == null) {

            // 保存平台属性
            this.save(baseAttrInfo) ;

            /**
             * stream流程的操作数据思路：获取流对象 -----> 进行中间操作 ----->  进行收集操作
             * map中间操作的方法的主要作用：就是对流中的元素类型进行转换
             */
            // 保存平台属性值
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            List<BaseAttrValue> baseAttrValues = attrValueList.stream().map(baseAttrValue -> {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                return baseAttrValue;
            }).collect(Collectors.toList());
            baseAttrValueService.saveBatch(baseAttrValues) ;

        }else {  // 执行修改操作

            this.updateById(baseAttrInfo) ;     // 修改平台属性名称

            // 删除之前的平台属性值
            LambdaQueryWrapper<BaseAttrValue> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BaseAttrValue::getAttrId , baseAttrInfo.getId()) ;
            baseAttrValueService.remove(lambdaQueryWrapper) ;

            // 新增平台属性值
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            List<BaseAttrValue> baseAttrValues = attrValueList.stream().map(baseAttrValue -> {
                baseAttrValue.setId(null);
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                return baseAttrValue;
            }).collect(Collectors.toList());
            baseAttrValueService.saveBatch(baseAttrValues) ;
        }

    }

}




