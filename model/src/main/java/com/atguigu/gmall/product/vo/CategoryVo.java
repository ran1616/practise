package com.atguigu.gmall.product.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryVo implements Serializable  {

    private Long categoryId ;
    private String categoryName ;
    private List<CategoryVo> categoryChild ;      // categoryId , categoryName , List<> categoryChild

}
