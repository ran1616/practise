package com.atguigu.gmall.search.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "test_person")
public class Person {

    @Id
    private Long id;

    /**
     * String在es索引库中表示的方式：
     * text: 可以对字段进行分词
     * keyword: 不可以对字段进行分词
     */
    @Field(name = "username" , index = true , type = FieldType.Text , analyzer = "ik_smart")
    private String username;

    @Field(name = "address" , index = true , type = FieldType.Text , analyzer = "ik_smart")
    private String address;

    @Field(name = "age" , index = true , type = FieldType.Integer)
    private Integer age;

}