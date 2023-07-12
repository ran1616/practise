package com.atguigu.gmall.product.service.impl;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.atguigu.gmall.product.dto.SkuInfoDto;
import com.atguigu.gmall.product.entity.SkuAttrValue;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuSaleAttrValue;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2023-07-10 16:46:04
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService{

    @Autowired
    private SkuImageService skuImageService ;

    @Autowired
    private SkuAttrValueService skuAttrValueService ;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService ;

    @Override
    public Page findByPage(Integer pageNo, Integer pageSize) {
        Page page = new Page(pageNo , pageSize) ;
        this.page(page) ;
        return page;
    }

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfoDto skuInfoDto) {

        // 保存sku的基本信息
        SkuInfo skuInfo = new SkuInfo() ;
        BeanUtils.copyProperties(skuInfoDto , skuInfo);
        skuInfo.setIsSale(0);
        this.save(skuInfo) ;

        // 保存sku的图片数据
        List<SkuImage> skuImageList = skuInfoDto.getSkuImageList();
        skuImageList = skuImageList.stream().map(skuImage -> {
            skuImage.setSkuId(skuInfo.getId());
            return skuImage ;
        }).collect(Collectors.toList()) ;
        skuImageService.saveBatch(skuImageList) ;

        // 保存sku的平台属性值数据
        List<SkuAttrValue> skuAttrValueList = skuInfoDto.getSkuAttrValueList();
        skuAttrValueList = skuAttrValueList.stream().map(skuAttrValue -> {
            skuAttrValue.setSkuId(skuInfo.getId());
            return skuAttrValue ;
        }).collect(Collectors.toList()) ;
        skuAttrValueService.saveBatch(skuAttrValueList) ;

        // 保存sku的销售属性值数据
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfoDto.getSkuSaleAttrValueList();
        skuSaleAttrValueList = skuSaleAttrValueList.stream().map(skuSaleAttrValue -> {
            skuSaleAttrValue.setSkuId(skuInfo.getId());
            skuSaleAttrValue.setSpuId(skuInfoDto.getSpuId());
            return skuSaleAttrValue ;
        }).collect(Collectors.toList()) ;
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList) ;

    }

}




