package com.atguigu.gmall.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * ES的客户端：
 * 1、es官方所提供的java的客户端:  Java High Level REST Client
 * 2、spring data es: spring针对es所提供的java客户端进行了封装，简化es的操作，
 *    方式一：通过ElasticsearchRestTemplate操作ES，可以进行复杂的搜索
 *    方式二：通过ElasticsearchRepository接口中所提供的方法操作ES，基本操作较为方便，复杂的搜索
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class ,args) ;
    }

}
