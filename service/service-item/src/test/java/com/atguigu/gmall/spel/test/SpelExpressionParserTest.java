package com.atguigu.gmall.spel.test;

import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Date;

public class SpelExpressionParserTest {


    /**
     * 让表达式变的相对比较灵活，那么此时就可以向SpelExpressionParser中预存一些数据，表达式就可以从表达式解析器中获取到预存的数据
     * 预存的是什么数据，那么此时获取到的就是什么数据
     */
    @Test
    public void spelExpressionParser03() {
        String s = "sku-detail:#{#params[0]}" ;
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser() ;
        Expression expression = spelExpressionParser.parseExpression(s, ParserContext.TEMPLATE_EXPRESSION);
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable("params" , new String[]{"49" , "50" , "haha"});
        evaluationContext.setVariable("args" , "50");
        evaluationContext.setVariable("date" , new Date());
        String value = expression.getValue(evaluationContext, String.class);
        System.out.println(value);
    }

    @Test
    public void spelExpressionParser02() {

        String s = "sku-detail:#{'atguigu'.toUpperCase()}:#{T(java.util.UUID).randomUUID().toString().replace('-', '')}" ;
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser() ;        // 表达式解析器对象
        Expression expression = spelExpressionParser.parseExpression(s, ParserContext.TEMPLATE_EXPRESSION);                // 得到一个表达式对象
        String value = expression.getValue(String.class);                               // 从表达式对象中获取解析以后的结果
        System.out.println(value);

    }

    @Test
    public void spelExpressionParser01() {

        String s = "'atguigu'" ;
        s = "1 + 1" ;
        s = "'atguigu'.toUpperCase()" ;
        s = "T(java.util.UUID).randomUUID().toString().replace('-', '')" ;
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser() ;        // 表达式解析器对象
        Expression expression = spelExpressionParser.parseExpression(s);                // 得到一个表达式对象
        String value = expression.getValue(String.class);                               // 从表达式对象中获取解析以后的结果
        System.out.println(value);

    }

}
