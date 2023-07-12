package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.dto.SpuInfoDto;
import com.atguigu.gmall.product.entity.SpuImage;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.entity.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.SpuInfo;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【spu_info(商品表)】的数据库操作Service实现
* @createDate 2023-07-10 16:46:04
*/
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo> implements SpuInfoService {

    @Autowired
    private SpuImageService spuImageService ;

    @Autowired
    private SpuSaleAttrService spuSaleAttrService ;

    @Autowired
    private SpuSaleAttrValueService spuSaleAttrValueService ;

    @Override
    public Page findByPage(Integer pageNo, Integer pageSize , Long category3Id) {
        Page page = new Page(pageNo , pageSize) ;
        LambdaQueryWrapper<SpuInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(SpuInfo::getCategory3Id , category3Id) ;
        this.page(page , lambdaQueryWrapper) ;
        return page;
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfoDto spuInfoDto) {

        // 保存spu的基本数据
        SpuInfo spuInfo = new SpuInfo() ;
//        spuInfo.setSpuName(spuInfoDto.getSpuName());
//        spuInfo.setDescription(spuInfoDto.getDescription());
//        spuInfo.setCategory3Id(spuInfoDto.getCategory3Id());
//        spuInfo.setTmId(spuInfoDto.getTmId());
        BeanUtils.copyProperties(spuInfoDto , spuInfo);         // 把SpuInfoDto对象的属性值copy到spuInfo对象中, 条件：同名同类型
        this.save(spuInfo) ;

        // 保存spu的图片数据
        List<SpuImage> spuImageList = spuInfoDto.getSpuImageList();
        spuImageList = spuImageList.stream().map(spuImage -> {
            spuImage.setSpuId(spuInfo.getId());
            return spuImage ;
        }).collect(Collectors.toList()) ;
        spuImageService.saveBatch(spuImageList) ;

        // 保存spu的销售属性名
        List<SpuSaleAttr> spuSaleAttrList = spuInfoDto.getSpuSaleAttrList();
        spuSaleAttrList = spuSaleAttrList.stream().map(spuSaleAttr -> {
            spuSaleAttr.setSpuId(spuInfo.getId());
            return spuSaleAttr ;
        }).collect(Collectors.toList()) ;
        spuSaleAttrService.saveBatch(spuSaleAttrList) ;

        // 保存spu的销售属性值
        spuSaleAttrList.stream().forEach(spuSaleAttr -> {
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            spuSaleAttrValueList = spuSaleAttrValueList.stream().map(spuSaleAttrValue -> {
                spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                spuSaleAttrValue.setSpuId(spuInfo.getId());
                return spuSaleAttrValue ;
            }).collect(Collectors.toList()) ;
            spuSaleAttrValueService.saveBatch(spuSaleAttrValueList) ;
        });

    }
}


























