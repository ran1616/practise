package com.atguigu.gmall.search.biz.impl;
import cn.hutool.core.util.PageUtil;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.google.common.collect.Lists;
import com.atguigu.gmall.search.vo.SearchOrderMapVo;

import com.atguigu.gmall.search.biz.GoodsBizService;
import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GoodsBizServiceImpl implements GoodsBizService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate ;

    @Autowired
    private GoodsRepository goodsRepository ;

    @Override
    public void saveGoods(Goods goods) {
        goodsRepository.save(goods) ;
    }

    @Override
    public void deleteById(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    @Override
    public SearchResponseVo search(SearchParamDTO searchParamDTO) {

        log.info("GoodsBizServiceImpl...search...方法执行了...");

        // 通过elasticsearchRestTemplate操作Es实现搜索封装结果数据到searchResponseVo对象
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 构建分类的搜索条件
        Long category1Id = searchParamDTO.getCategory1Id();
        if(category1Id != null) {
            boolQuery.must(QueryBuilders.termQuery("category1Id" , category1Id)) ;
        }

        Long category2Id = searchParamDTO.getCategory2Id();
        if(category2Id != null) {
            boolQuery.must(QueryBuilders.termQuery("category2Id" , category2Id)) ;
        }

        Long category3Id = searchParamDTO.getCategory3Id();
        if(category3Id != null) {
            boolQuery.must(QueryBuilders.termQuery("category3Id" , category3Id)) ;
        }

        // 关键字的搜索条件
        String keyword = searchParamDTO.getKeyword();
        if(!StringUtils.isEmpty(keyword)) {
            boolQuery.must(QueryBuilders.matchQuery("title" , keyword)) ;
        }

        // 构建品牌的搜索条件
        String trademark = searchParamDTO.getTrademark();
        if(!StringUtils.isEmpty(trademark)) {
            String[] trademarkArr = trademark.split(":");
            long tmId = Long.parseLong(trademarkArr[0]);
            boolQuery.must(QueryBuilders.termQuery("tmId" , tmId)) ;
        }

        // 构建平台属性的搜索条件
        String[] props = searchParamDTO.getProps();
        if(props != null && props.length > 0) {
            for(String prop : props) {  // 8023:冰川蓝:颜色   ----> 平台属性的Id:平台属性的值:平台属性名称
                String[] propArr = prop.split(":");
                Long attrId = Long.parseLong(propArr[0]);
                String attrValueName = propArr[1] ;
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId" , attrId)) ;
                boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrValue" , attrValueName)) ;
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", boolQueryBuilder, ScoreMode.None);
                boolQuery.must(nestedQuery) ;
            }
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQuery).build() ;

        // 构建排序搜索条件
        String order = searchParamDTO.getOrder();
        if(!StringUtils.isEmpty(order)) {       // 1:desc
            Sort sort = null ;
            String[] orderArr = order.split(":");
            String field = orderArr[0] ;
            String direction = orderArr[1] ;
            switch (field) {
                case "1" :
                    sort = Sort.by(  "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC , "hotScore") ;
                    break;
                case "2":
                    sort = Sort.by(  "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC , "price") ;
                    break;
            }
            nativeSearchQuery.addSort(sort) ;
        }

        // 设置分页参数
        PageRequest pageRequest = PageRequest.of(searchParamDTO.getPageNo() - 1, searchParamDTO.getPageSize());
        nativeSearchQuery.setPageable(pageRequest) ;

        SearchHits<Goods> goodsSearchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Goods.class);

        // 解析结果
        SearchResponseVo searchResponseVo = parseResponse(goodsSearchHits , searchParamDTO) ;

        // 返回结果数据
        return searchResponseVo;
    }

    private SearchResponseVo parseResponse(SearchHits<Goods> goodsSearchHits, SearchParamDTO searchParamDTO) {

        SearchResponseVo searchResponseVo = new SearchResponseVo() ;
        searchResponseVo.setSearchParam(searchParamDTO);

        // 品牌的面包屑
        String trademark = searchParamDTO.getTrademark();
        if(!StringUtils.isEmpty(trademark)) {
            String[] trademarkArr = trademark.split(":");
            String tmName = trademarkArr[1] ;
            searchResponseVo.setTrademarkParam("品牌:" + tmName);
        }

        // 平台属性面包屑
        String[] props = searchParamDTO.getProps();
        if(props != null && props.length > 0) {
            List<SearchAttr> searchAttrList = new ArrayList<>() ;
            for(String prop : props) {          // 8023:冰川蓝:颜色   ----> 平台属性的Id:平台属性的值:平台属性名称
                String[] propArr = prop.split(":");
                Long attrId = Long.parseLong(propArr[0]);
                String attrValueName = propArr[1] ;
                String attrName = propArr[2] ;
                SearchAttr searchAttr = new SearchAttr(attrId , attrValueName , attrName) ;
                searchAttrList.add(searchAttr) ;
            }
            searchResponseVo.setPropsParamList(searchAttrList);
        }

        // 当前页数据
        List<SearchHit<Goods>> searchHits = goodsSearchHits.getSearchHits();
        List<Goods> goodsList = searchHits.stream().map(searchHit -> searchHit.getContent()).collect(Collectors.toList());
        searchResponseVo.setGoodsList(goodsList);

        // 排序结果
        String order = searchParamDTO.getOrder();
        SearchOrderMapVo searchOrderMapVo = new SearchOrderMapVo("1" , "desc") ;
        if(!StringUtils.isEmpty(order)) {
            String[] orderAttr = order.split(":");
            searchOrderMapVo = new SearchOrderMapVo(orderAttr[0] , orderAttr[1]) ;
        }
        searchResponseVo.setOrderMap(searchOrderMapVo);

        // 分页结果参数
        searchResponseVo.setPageNo(searchParamDTO.getPageNo());
        Long totalHits = goodsSearchHits.getTotalHits();        // 总记录数
        int totalPage = PageUtil.totalPage(totalHits.intValue(), searchParamDTO.getPageSize());
        searchResponseVo.setTotalPages(totalPage);      // 总页数

        // TODO 品牌列表
        searchResponseVo.setTrademarkList(Lists.newArrayList());

        // TODO 平台属性列表
        searchResponseVo.setAttrsList(Lists.newArrayList());

        // TODO UrlParam
        searchResponseVo.setUrlParam("");

        return searchResponseVo ;

    }

}
