package com.atguigu.gmall.search.vo;

import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchAttr;
import lombok.Data;

import java.util.List;

@Data
public class SearchResponseVo {

    private SearchParamDTO searchParam ;

    // 品牌的面包屑
    private String  trademarkParam ;

    // 平台属性面包屑
    private List<SearchAttr> propsParamList ;

    // 品牌列表
    private List<SearchTmVo> trademarkList ;

    // 平台属性列表
    private List<SearchRespAttrVo> attrsList ;

    // 定义urlParam属性
    private String urlParam ;

    // 排序属性
    private  SearchOrderMapVo orderMap ;

    // 商品数据
    private List<Goods> goodsList ;

    // 分页结果数据
    private Integer pageNo ;

    // 总页数
    private Integer totalPages ;

}
