package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.gateway.properties.UserAuthProperties;
import com.atguigu.gmall.user.entity.UserInfo;
import com.fasterxml.jackson.databind.introspect.Annotated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class UserAuthFilter implements GlobalFilter , Ordered {

    @Autowired
    private UserAuthProperties userAuthProperties ;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    private AntPathMatcher antPathMatcher = new AntPathMatcher() ;

    /**
     * 拦截所有请求
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 获取请求路径
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String path = serverHttpRequest.getURI().getPath();
        log.info("UserAuthFilter...filter方法执行了..., path: {}" , path);

        // 不需要验证用户登录的路径规则的处理
        List<String> noauthurls = userAuthProperties.getNoauthurls() ;
        for(String noauthurl : noauthurls) {        // /css/**、/js/**、/img/**
            boolean match = antPathMatcher.match(noauthurl, path);
            if(match) {  // 满足规则直接进行放行
                return chain.filter(exchange);
            }
        }

        // 需要验证用户登录的路径规则的处理
        List<String> authurls = userAuthProperties.getAuthurls();
        for(String authurl : authurls) {
            boolean match = antPathMatcher.match(authurl, path);
            if(match) {     // 验证登录

                // 获取token
                String token = getToken(exchange) ;
                if(StringUtils.isEmpty(token)) {            // 未登录 , 进行请求重定向，把用户踢回到登录页面
                    // 把用户踢回到登录页面
                    return locationUrl(exchange , userAuthProperties.getToLoginPage()) ;
                }else {         // 有token，需要校验token的合法性
                    UserInfo userInfo = getUserInfoByToken(token) ;
                    if(userInfo == null) {      // 伪造的token，进行请求重定向，把用户踢回到登录页面
                        // 把用户踢回到登录页面
                        return locationUrl(exchange , userAuthProperties.getToLoginPage()) ;
                    }else {
                        return userIdThrought(exchange , chain , userInfo) ;
                    }
                }

            }
        }

        // 普通资源： 就是用户在登录和未登录状态下都可以进行访问的资源
        String token = getToken(exchange);
        if(StringUtils.isEmpty(token)) {            // 用户未登录直接进行放行
            return userTempIdThrought(exchange , chain);
        }else {
            UserInfo userInfo = getUserInfoByToken(token);
            if(userInfo == null) {                // 伪令牌
                return locationUrl(exchange , userAuthProperties.getToLoginPage()) ;
            }else {                               // 用户登录放行,在放行之前需要进行用户id的透传
                return  userIdThrought(exchange , chain , userInfo) ;
            }
        }

    }

    private Mono<Void> userTempIdThrought(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 获取临时用户的id
        String userTempId = getUserTempId(exchange) ;

        // 创建一个新的Request对象
        // mutate方法获取一个建造者对象
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                .header("userTempId" , userTempId)
                .build();

        // 创建一个新的exchange对象
        ServerWebExchange webExchange = exchange.mutate().request(serverHttpRequest).response(exchange.getResponse()).build();

        // 放行
        return chain.filter(webExchange) ;

    }

    // 进行请求放行，在放行之前进行用户id的透传
    /**
     * 透传的思路：创建一个新的Request对象，然后在该对象的请求头中添加一个新的请求头userId , 在放行的时候使用新的request对象
     */
    private Mono<Void> userIdThrought(ServerWebExchange exchange, GatewayFilterChain chain, UserInfo userInfo) {

        // 获取临时用户的id
        String userTempId = getUserTempId(exchange) ;

        // 创建一个新的Request对象
        // mutate方法获取一个建造者对象
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                .header("userId", String.valueOf(userInfo.getId()))
                .header("userTempId" , userTempId)
                .build();

        // 创建一个新的exchange对象
        ServerWebExchange webExchange = exchange.mutate().request(serverHttpRequest).response(exchange.getResponse()).build();

        // 放行
        return chain.filter(webExchange) ;

    }

    // 获取临时用户的id
    private String getUserTempId(ServerWebExchange exchange) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        HttpCookie httpCookie = serverHttpRequest.getCookies().getFirst("userTempId");
        if(httpCookie != null) {
            return httpCookie.getValue() ;
        }else {
            String userTempId = serverHttpRequest.getHeaders().getFirst("userTempId");        // 从请求头中获取token
            return userTempId ;
        }
    }

    // 把用户踢回到登录页面
    private Mono<Void> locationUrl(ServerWebExchange exchange, String toLoginPage) {

        // 获取访问当前资源的请求路径
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String originUrl = serverHttpRequest.getURI().toString();

        // 进行请求重定向 ====> 302状态码 + location响应头
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.setStatusCode(HttpStatus.FOUND) ;            // 设置302的响应状态码
        serverHttpResponse.getHeaders().set("location" , toLoginPage + "?originUrl=" + originUrl);

        // 给serverHttpResponse添加新的Cookie
        ResponseCookie tokenResponseCookie = ResponseCookie.from("token", "").domain("gmall.com").maxAge(-1).path("/").build();
        ResponseCookie userInfoResponseCookie = ResponseCookie.from("userInfo", "").domain("gmall.com").maxAge(-1).path("/").build();
        serverHttpResponse.addCookie(tokenResponseCookie);
        serverHttpResponse.addCookie(userInfoResponseCookie);

        return serverHttpResponse.setComplete() ;       // 不进行放行，结束请求

    }

    // 根据token获取用户的信息
    private UserInfo getUserInfoByToken(String token) {
        String userInfoJSON = redisTemplate.opsForValue().get(GmallConstant.REDIS_USER_LOGIN_PRE + token);
        if(StringUtils.isEmpty(userInfoJSON)) {
            return null ;
        }else {
            UserInfo userInfo = JSON.parseObject(userInfoJSON, UserInfo.class);
            return userInfo ;
        }
    }

    // 获取token
    /**
     *  前端传递token的方式：
     *  1、通过请求头(token)进行传递
     *  2、通过Cookie进行传递的
     */
    private String getToken(ServerWebExchange exchange) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        HttpCookie httpCookie = serverHttpRequest.getCookies().getFirst("token");
        if(httpCookie != null) {
           return httpCookie.getValue() ;
        }else {
            String token = serverHttpRequest.getHeaders().getFirst("token");        // 从请求头中获取token
            return token ;
        }
    }

    /**
     * 定义过滤器的编号，后期框架会调用该方法获取当前过滤器的编号，然后根据这个编号对过滤器进行排序，后期就按照排序以后的结果依次执行对应的过滤器
     * @return
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
