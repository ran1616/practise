package com.atguigu.gamll.search.test;

import com.atguigu.gmall.search.SearchApplication;
import com.atguigu.gmall.search.biz.GoodsBizService;
import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SearchApplication.class )
public class GoodsEsTest {

    @Autowired
    private GoodsBizService goodsBizService ;

    @Test
    public void search() {

        SearchParamDTO searchParamDTO = new SearchParamDTO() ;
//        searchParamDTO.setCategory1Id(2L);
//        searchParamDTO.setCategory2Id(0L);
//        searchParamDTO.setCategory3Id(0L);
        searchParamDTO.setKeyword("华为手机");

        searchParamDTO.setTrademark("2:华为");
        searchParamDTO.setProps(new String[]{"3:8GB:运行内存" , "4:256GB:机身存储"});
        searchParamDTO.setOrder("2:desc");
        searchParamDTO.setPageNo(1);
        searchParamDTO.setPageSize(3);

        SearchResponseVo searchResponseVo = goodsBizService.search(searchParamDTO);
        System.out.println(searchResponseVo);

    }

}
