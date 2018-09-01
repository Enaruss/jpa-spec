package com.russ.forkwenhao.jpa.integration;

import com.russ.forkwenhao.jpa.SpecificationsEX;
import com.russ.forkwenhao.jpa.builder.PersonBuilder;
import com.russ.forkwenhao.jpa.model.Person;
import com.russ.forkwenhao.jpa.model.PersonIdCard;
import com.russ.forkwenhao.jpa.repository.PersonIdCardRepository;
import com.russ.forkwenhao.jpa.repository.PersonRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class VirtualViewTest {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PersonIdCardRepository personIdCardRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void should_be_able_to_query_from_virtual_view() {
        // given
        Person jack = new PersonBuilder()
            .name("Jack")
            .age(18)
            .idCard("100000000000000000")
            .build();
        Person eric = new PersonBuilder()
            .name("Eric")
            .age(20)
            .idCard("200000000000000000")
            .build();
        Person jackson = new PersonBuilder()
            .age(30)
            .nickName("Jackson")
            .idCard("300000000000000000")
            .build();
        personRepository.save(jack);
        personRepository.save(eric);
        personRepository.save(jackson);
        entityManager.flush();

        // when
        Specification<PersonIdCard> specification = SpecificationsEX.<PersonIdCard>and()
            .gt("age", 18)
            .build();
        List<PersonIdCard> personIdCards = personIdCardRepository.findAll(specification);

        // then
        assertThat(personIdCards.size()).isEqualTo(2);
    }
}
