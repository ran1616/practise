package com.atguigu.gamll.search.test;

import com.atguigu.gmall.search.SearchApplication;
import com.atguigu.gmall.search.domain.Person;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import java.util.List;

@SpringBootTest(classes = SearchApplication.class)
public class ElasticsearchRestTemplateTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate ;

    @Test
    public void seach() {

        // FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort("age").order(SortOrder.DESC);
        PageRequest pageRequest = PageRequest.of(0, 2);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("age").gt(1).lt(6))
                .withPageable(pageRequest)
                // .withSort(fieldSortBuilder)
                .build() ;
        nativeSearchQuery.addSort(Sort.by(Sort.Direction.DESC , "age"));
        SearchHits<Person> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Person.class);
        long totalHits = searchHits.getTotalHits();
        System.out.println("count:" + totalHits);
        List<SearchHit<Person>> searchHitList = searchHits.getSearchHits();
        for (SearchHit<Person> searchHit : searchHitList) {
            Person person = searchHit.getContent();
            System.out.println(person);
        }
    }

    @Test
    public void delete() {
        elasticsearchRestTemplate.delete("1" , IndexCoordinates.of("test_person")) ;
    }

    @Test
    public void update() {
        Document document = Document.create();
        document.put("username" , "尚硅谷高端IT教育") ;
        document.put("address" , "高新区科技五路") ;
        UpdateQuery updateQuery = UpdateQuery.builder("1").withDocument(document).build();
        elasticsearchRestTemplate.update(updateQuery , IndexCoordinates.of("test_person")) ;
    }

    @Test
    public void get() {
        Person person = elasticsearchRestTemplate.get("1", Person.class);
        System.out.println(person);
    }

    @Test
    public void save() {
        Person person = new Person() ;
        person.setId(1L);
        person.setUsername("尚硅谷IT教育");
        person.setAddress("高薪区科技五路");
        person.setAge(13);
        elasticsearchRestTemplate.save(person) ;
    }

    @Test
    public void save2() {
        for(int x = 1 ; x <= 10 ; x++) {
            Person person = new Person() ;
            person.setId(Long.parseLong(String.valueOf(x)));
            person.setUsername("尚硅谷IT教育" + x);
            person.setAddress("高薪区科技五路" + x);
            person.setAge(x);
            elasticsearchRestTemplate.save(person) ;
        }
    }
}
