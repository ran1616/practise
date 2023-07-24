package com.atguigu.gamll.search.test;

import com.atguigu.gmall.search.SearchApplication;
import com.atguigu.gmall.search.domain.Person;
import com.atguigu.gmall.search.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest(classes = SearchApplication.class)
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository ;

    @Test
    public void delete() {
        personRepository.deleteById(14L);
    }

    @Test
    public void update() {
        Optional<Person> optional = personRepository.findById(14L);
        Person person = optional.get();
        person.setUsername("尚硅谷IT教育2");
        person.setAddress("高薪区科技五路2");
        personRepository.save(person) ;
    }

    @Test
    public void get() {
        Optional<Person> optional = personRepository.findById(14L);
        Person person = optional.get();
        System.out.println(person);
    }

    @Test
    public void save() {
        Person person = new Person() ;
        person.setId(14L);
        person.setUsername("尚硅谷IT教育");
        person.setAddress("高薪区科技五路");
        person.setAge(14);
        personRepository.save(person) ;
    }

}
