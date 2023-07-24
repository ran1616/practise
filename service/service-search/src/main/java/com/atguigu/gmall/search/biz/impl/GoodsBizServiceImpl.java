package com.atguigu.gmall.search.biz.impl;
import cn.hutool.core.util.PageUtil;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.atguigu.gmall.search.vo.SearchRespAttrVo;
import com.atguigu.gmall.search.vo.SearchTmVo;
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
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        // 设置高亮的参数
        HighlightBuilder highlightBuilder = new HighlightBuilder() ;
        highlightBuilder.field("title") ;
        highlightBuilder.preTags("<font color='red'>") ;
        highlightBuilder.postTags("</font>") ;

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withHighlightBuilder(highlightBuilder)
                .build() ;

        // 构建排序搜索条件
        String order = searchParamDTO.getOrder();
        if(!StringUtils.isEmpty(order) && !"null".equals(order)) {       // 1:desc
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

        // 设置聚合的参数
//        TermsAggregationBuilder termsAggregationBuilder = new TermsAggregationBuilder("tmIdAgg" , ValueType.LONG) ;
//        termsAggregationBuilder.field("tmId") ;
//        termsAggregationBuilder.size(100) ;
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId").size(100);
        termsAggregationBuilder.subAggregation(AggregationBuilders.terms("tmNameAgg").size(10).field("tmName")) ;
        termsAggregationBuilder.subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").size(10).field("tmLogoUrl")) ;
        nativeSearchQuery.addAggregation(termsAggregationBuilder);


        // 设置平台属性聚合参数
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAggregationBuilder = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(100);
        attrIdAggregationBuilder.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(10)) ;
        attrIdAggregationBuilder.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(100)) ;
        nestedAggregationBuilder.subAggregation(attrIdAggregationBuilder) ;
        nativeSearchQuery.addAggregation(nestedAggregationBuilder);

        SearchHits<Goods> goodsSearchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Goods.class);

        // 解析结果
        SearchResponseVo searchResponseVo = parseResponse(goodsSearchHits , searchParamDTO) ;

        // 返回结果数据
        return searchResponseVo;
    }

    @Override
    public void updateHotScore(Long skuId, Long hotScore) {
        Optional<Goods> optional = goodsRepository.findById(skuId);
        Goods goods = optional.get();
        goods.setHotScore(hotScore);
        goodsRepository.save(goods) ;
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
        List<Goods> goodsList = searchHits.stream().map(searchHit -> {

            Goods goods = searchHit.getContent();       // 元素文档数据
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            if(highlightFields != null && highlightFields.size() > 0) {
                List<String> valueList = highlightFields.get("title");
                if(valueList != null && valueList.size() > 0) {
                    String highlightTitle = valueList.get(0);
                    goods.setTitle(highlightTitle);
                }
            }
            return goods ;

        }).collect(Collectors.toList());
        searchResponseVo.setGoodsList(goodsList);

        // 排序结果
        String order = searchParamDTO.getOrder();
        SearchOrderMapVo searchOrderMapVo = new SearchOrderMapVo("1" , "desc") ;
        if(!StringUtils.isEmpty(order) && !"null".equals(order)) {
            String[] orderAttr = order.split(":");
            searchOrderMapVo = new SearchOrderMapVo(orderAttr[0] , orderAttr[1]) ;
        }
        searchResponseVo.setOrderMap(searchOrderMapVo);

        // 分页结果参数
        searchResponseVo.setPageNo(searchParamDTO.getPageNo());
        Long totalHits = goodsSearchHits.getTotalHits();        // 总记录数
        int totalPage = PageUtil.totalPage(totalHits.intValue(), searchParamDTO.getPageSize());
        searchResponseVo.setTotalPages(totalPage);      // 总页数

        // 品牌列表 ，解析结果获取聚合数据
        Aggregations aggregations = goodsSearchHits.getAggregations();
        ParsedLongTerms tmIdAggregation = aggregations.get("tmIdAgg");
        List<? extends Terms.Bucket> buckets = tmIdAggregation.getBuckets();
        List<SearchTmVo> searchTmVoList = new ArrayList<>() ;
        for(Terms.Bucket bucket : buckets) {

            // 品牌的id
            String tmIdStr = bucket.getKeyAsString();

            // 获取品牌的名称
            Aggregations bucketAggregations = bucket.getAggregations();
            ParsedStringTerms tmNameAggregation =  bucketAggregations.get("tmNameAgg") ;
            String tmName = tmNameAggregation.getBuckets().get(0).getKeyAsString() ;

            // 获取品牌的logoUrl
            ParsedStringTerms tmLogoUrlAggregation =  bucketAggregations.get("tmLogoUrlAgg") ;
            String tmLogoUrl = tmLogoUrlAggregation.getBuckets().get(0).getKeyAsString() ;

            // 创建SearchTmVo对象封装数据
            SearchTmVo searchTmVo = new SearchTmVo() ;
            searchTmVo.setTmId(Long.parseLong(tmIdStr));
            searchTmVo.setTmName(tmName);
            searchTmVo.setTmLogoUrl(tmLogoUrl);
            searchTmVoList.add(searchTmVo) ;

        }
        searchResponseVo.setTrademarkList(searchTmVoList);

        // UrlParam
        searchResponseVo.setUrlParam(buildUrlParam(searchParamDTO));

        // 解析结果获取平台属性聚合数据
        ParsedNested parsedNested = aggregations.get("attrAgg") ;
        Aggregations nestedAggregations = parsedNested.getAggregations();
        ParsedLongTerms attrIdAggregation = nestedAggregations.get("attrIdAgg") ;
        List<? extends Terms.Bucket> attrIdAggregationBuckets = attrIdAggregation.getBuckets();
        List<SearchRespAttrVo> searchRespAttrVoList = new ArrayList<>() ;
        for(Terms.Bucket bucket : attrIdAggregationBuckets) {

            // 获取平台属性的id
            Long attrId = Long.parseLong(bucket.getKeyAsString());

            // 获取平台属性名称
            Aggregations bucketAggregations = bucket.getAggregations();
            ParsedStringTerms parsedStringTerms = bucketAggregations.get("attrNameAgg") ;
            String attrName = parsedStringTerms.getBuckets().get(0).getKeyAsString();

            // 获取平台属性的值
            ParsedStringTerms attrValueAggTerms = bucketAggregations.get("attrValueAgg");
            List<? extends Terms.Bucket> aggTermsBuckets = attrValueAggTerms.getBuckets();
            List<String> attrValueList = new ArrayList<>() ;
            for(Terms.Bucket bu : aggTermsBuckets) {
                String attrValue = bu.getKeyAsString();
                attrValueList.add(attrValue) ;
            }

            // 创建SearchRespAttrVo对象封装数据
            SearchRespAttrVo searchRespAttrVo = new SearchRespAttrVo() ;
            searchRespAttrVo.setAttrId(attrId);
            searchRespAttrVo.setAttrName(attrName);
            searchRespAttrVo.setAttrValueList(attrValueList);
            searchRespAttrVoList.add(searchRespAttrVo) ;

        }
        searchResponseVo.setAttrsList(searchRespAttrVoList);

        return searchResponseVo ;

    }

    private String buildUrlParam(SearchParamDTO searchParamDTO) {       // 构建urlParam参数
        StringBuilder sb = new StringBuilder("list.html?") ;
        Long category1Id = searchParamDTO.getCategory1Id();
        if(category1Id != null) {
            sb.append("category1Id=" + category1Id) ;
        }

        Long category2Id = searchParamDTO.getCategory2Id();
        if(category2Id != null) {
            sb.append("category2Id=" + category2Id) ;
        }

        Long category3Id = searchParamDTO.getCategory3Id();
        if(category3Id != null) {
            sb.append("category3Id=" + category3Id) ;
        }

        String keyword = searchParamDTO.getKeyword();
        if(!StringUtils.isEmpty(keyword)) {
            sb.append("keyword=" + keyword) ;
        }

        String trademark = searchParamDTO.getTrademark();
        if(!StringUtils.isEmpty(trademark)) {
            sb.append("&trademark=" + trademark) ;
        }

        String[] props = searchParamDTO.getProps();
        if(props != null && props.length > 0) {
            for(String prop : props) {
                sb.append("&props=" + prop) ;
            }
        }

        return sb.toString() ;
    }


}
