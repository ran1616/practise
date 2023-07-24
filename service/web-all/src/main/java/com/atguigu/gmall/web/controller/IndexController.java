package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.feign.product.BaseCategoryFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private BaseCategoryFeignClient baseCategoryFeignClient ;

    @GetMapping(value = {"/" , "/index.html"} )
    public String index(Model model) {

        // 通过openFeign调用service-product微服务的接口查询三级分类数据
        Result<List<CategoryVo>> result = baseCategoryFeignClient.findAllCategoryTree();

        // 把查询到的结果存储到Model数据模型中
        List<CategoryVo> categoryVoList = result.getData();
        model.addAttribute("list" , categoryVoList) ;

        return "index/index" ;            // /templates/index/index.html
    }

}
