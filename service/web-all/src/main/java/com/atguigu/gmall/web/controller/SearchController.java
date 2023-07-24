package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.feign.seach.SearchFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class SearchController {

    @Autowired
    private SearchFeignClient searchFeignClient ;

    @GetMapping(value = "/list.html")
    public String search(SearchParamDTO searchParamDTO , Model model) {
        log.info("SearchController...search方法执行了...");

        // 通过openFeign远程调用search微服务的接口，实现搜索，获取搜索到的数据
        Result<SearchResponseVo> result = searchFeignClient.search(searchParamDTO);
        SearchResponseVo searchResponseVo = result.getData();

        // 把数据存储到Model数据模型中
        model.addAttribute("searchParam" , searchResponseVo.getSearchParam() ) ;
        model.addAttribute("trademarkParam" , searchResponseVo.getTrademarkParam() ) ;
        model.addAttribute("propsParamList" , searchResponseVo.getPropsParamList() ) ;
        model.addAttribute("trademarkList" , searchResponseVo.getTrademarkList() ) ;
        model.addAttribute("attrsList" , searchResponseVo.getAttrsList() ) ;
        model.addAttribute("urlParam" , searchResponseVo.getUrlParam() ) ;
        model.addAttribute("orderMap" , searchResponseVo.getOrderMap() ) ;
        model.addAttribute("goodsList" , searchResponseVo.getGoodsList() ) ;
        model.addAttribute("pageNo" , searchResponseVo.getPageNo() ) ;
        model.addAttribute("totalPages" , searchResponseVo.getTotalPages() ) ;

        return "list/index" ;
    }

}
