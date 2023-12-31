package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.dto.SpuInfoDto;
import com.atguigu.gmall.product.entity.SpuInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2023-07-10 16:46:04
*/
public interface SpuInfoService extends IService<SpuInfo> {

    public abstract Page findByPage(Integer pageNo, Integer pageSize , Long category3Id);

    public abstract void saveSpuInfo(SpuInfoDto spuInfoDto);
}
