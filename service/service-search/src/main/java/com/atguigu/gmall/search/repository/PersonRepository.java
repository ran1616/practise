package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.domain.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * T泛型：表示的是要操作的索引库所对应的实体类型
 * ID泛型：表示的实体类的主键类型
 */
public interface PersonRepository extends ElasticsearchRepository<Person , Long> {

}
