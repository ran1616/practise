package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.SpuImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_image(商品图片表)】的数据库操作Service
* @createDate 2023-07-10 16:46:04
*/
public interface SpuImageService extends IService<SpuImage> {

    public abstract List<SpuImage> findBySpuId(Long spuId);
}
