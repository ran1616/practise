package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.dto.SkuInfoDto;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2023-07-10 16:46:04
*/
public interface SkuInfoService extends IService<SkuInfo> {

    public abstract Page findByPage(Integer pageNo, Integer pageSize);

    public abstract  void saveSkuInfo(SkuInfoDto skuInfoDto);

    public abstract  void onSale(Long skuId);

    public abstract void cancelSale(Long skuId);
}
