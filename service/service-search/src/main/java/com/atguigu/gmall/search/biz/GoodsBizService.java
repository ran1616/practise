package com.atguigu.gmall.search.biz;

import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.vo.SearchResponseVo;

public interface GoodsBizService {

    void saveGoods(Goods goods);

    void deleteById(Long skuId);

    SearchResponseVo search(SearchParamDTO searchParamDTO);
}
