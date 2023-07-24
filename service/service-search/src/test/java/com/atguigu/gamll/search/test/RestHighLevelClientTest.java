package com.atguigu.gamll.search.test;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.search.SearchApplication;
import com.atguigu.gmall.search.domain.Person;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest(classes = SearchApplication.class)
public class RestHighLevelClientTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient ;

    @Test
    public void search() throws IOException {
        SearchRequest searchRequest = new SearchRequest("person") ;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder() ;
        searchSourceBuilder.query(QueryBuilders.rangeQuery("age").gte(2).lte(6)) ;
        searchSourceBuilder.from(2) ;            // 搜索的数据的开始行号： (当前页码 - 1) * 每页显示的数据条数
        searchSourceBuilder.size(2) ;             // 每页显示的数据条数
        searchSourceBuilder.sort("age" , SortOrder.DESC ) ;
        searchRequest.source(searchSourceBuilder) ;
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        long count = searchHits.getTotalHits().value ;      // 返回满足条件的总记录数
        System.out.println("count:" + count) ;
        SearchHit[] searchHitsHits = searchHits.getHits();
        for(SearchHit searchHit : searchHitsHits) {
            String result = searchHit.getSourceAsString();
            System.out.println(result);
        }

    }

    @Test
    public void delete() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("person" , "1") ;
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse);
    }

    @Test
    public void update() throws IOException {

        Person person = new Person() ;
        person.setId(1L);
        person.setUsername("尚硅谷IT教育2");
        person.setAddress("科技五路2");
        person.setAge(13);

        UpdateRequest updateRequest = new UpdateRequest("person" , "1") ;
        updateRequest.doc(JSON.toJSONString(person) , XContentType.JSON) ;
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateResponse);
    }

    @Test
    public void get() throws IOException {
        GetRequest getRequest = new GetRequest("person", "1") ;
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        String result = getResponse.getSourceAsString();
        System.out.println(result);
    }

    @Test
    public void save() throws IOException {
        Person person = new Person() ;
        person.setId(1L);
        person.setUsername("尚硅谷IT教育");
        person.setAddress("科技五路");
        person.setAge(10);
        IndexRequest indexRequest = new IndexRequest("person").id("1") ;
        indexRequest.source(JSON.toJSONString(person) , XContentType.JSON) ;
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse);
    }

    @Test
    public void save2() throws IOException {

        for(int x = 1 ; x <= 10 ; x++) {
            Person person = new Person() ;
            person.setId(Long.parseLong(String.valueOf(x)));
            person.setUsername("尚硅谷IT教育" + x);
            person.setAddress("科技五路" + x);
            person.setAge(1 + x);
            IndexRequest indexRequest = new IndexRequest("person").id(String.valueOf(x)) ;
            indexRequest.source(JSON.toJSONString(person) , XContentType.JSON) ;
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            System.out.println(indexResponse);
        }

    }

}
