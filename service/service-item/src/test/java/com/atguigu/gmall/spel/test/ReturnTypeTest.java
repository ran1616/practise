package com.atguigu.gmall.spel.test;

import com.atguigu.gmall.product.vo.SkuDetailVo;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnTypeTest {

    public SkuDetailVo show() {
        return new SkuDetailVo() ;
    }

    public List<SkuDetailVo> function() {
        return new ArrayList<>() ;
    }

    public Map<String , List<SkuDetailVo>> method() {
        return new HashMap<>() ;
    }

    public static void main(String[] args) throws Exception {
        Class<ReturnTypeTest> clazz = ReturnTypeTest.class;
        Method method = clazz.getDeclaredMethod("method");
        Class returnType = method.getReturnType();
        Type genericReturnType = method.getGenericReturnType();
        System.out.println(returnType);
        System.out.println(genericReturnType);
    }

}
