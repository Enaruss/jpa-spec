package com.russ.forkwenhao.jpa.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.russ.forkwenhao.jpa.SpecificationsEX;
import com.russ.forkwenhao.jpa.builder.PersonBuilder;
import com.russ.forkwenhao.jpa.model.Person;
import com.russ.forkwenhao.jpa.repository.PersonRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GreatEqualTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void should_be_able_to_find_by_using_great_equal() {
        // given
        Person jack = new PersonBuilder()
            .name("Jack")
            .age(20)
            .build();
        Person eric = new PersonBuilder()
            .name("Eric")
            .age(18)
            .build();
        personRepository.save(jack);
        personRepository.save(eric);

        // when
        Specification<Person> specification = SpecificationsEX.<Person>and()
            .ge("age", 20)
            .build();

        List<Person> persons = personRepository.findAll(specification);

        // then
        assertThat(persons.size()).isEqualTo(1);
    }
}
