package com.russ.forkwenhao.jpa.integration;

import com.russ.forkwenhao.jpa.SpecificationsEX;
import com.russ.forkwenhao.jpa.builder.PersonBuilder;
import com.russ.forkwenhao.jpa.model.Person;
import com.russ.forkwenhao.jpa.model.Phone;
import com.russ.forkwenhao.jpa.repository.PersonRepository;
import com.russ.forkwenhao.jpa.repository.PhoneRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PredicateTest {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PhoneRepository phoneRepository;

    @Test
    public void should_be_able_to_find_by_using_many_to_one_query() {
        // given
        final Person jack = new PersonBuilder()
                .name("Jack")
                .age(18)
                .phone("iPhone", "139000000000")
                .phone("HuaWei", "13600000000")
                .phone("HuaWei", "18000000000")
                .phone("Samsung", "13600000000")
                .build();

        Set<Phone> jackPhones = jack.getPhones();
        for (Phone phone : jackPhones) {
            phone.setPerson(jack);
        }
        personRepository.save(jack);


        // when
        Specification<Phone> specification = SpecificationsEX.<Phone>and()
                .eq("brand", "HuaWei")
                .predicate(StringUtils.isNotBlank(jack.getName()), (Specification<Phone>) (root, query, cb) -> {
                    Path<Person> person = root.get("person");
                    return cb.equal(person.get("name"), jack.getName());
                })
                .build();

        List<Phone> phones = phoneRepository.findAll(specification);

        // then
        assertThat(phones.size()).isEqualTo(2);
    }

    @Test
    public void should_be_able_to_find_by_using_many_to_many_query() {
        // given
        Person jack = new PersonBuilder()
                .name("Jack")
                .age(18)
                .address("Sichuan", 3)
                .address("Sichuan", 5)
                .address("Chengdu", 4)
                .address("Zhonghe", 7)
                .build();

        Person eric = new PersonBuilder()
                .name("Eric")
                .age(20)
                .address("GaoXin", 8)
                .address("Tianfu", 9)
                .address("Chengdu", 4)
                .build();

        Person alex = new PersonBuilder()
                .name("Alex")
                .age(30)
                .address("HuaYang", 1)
                .address("NeiJiang", 2)
                .build();

        personRepository.save(jack);
        personRepository.save(eric);
        personRepository.save(alex);

        // when
        Specification<Person> specification = SpecificationsEX.<Person>and()
                .between("age", 10, 35)
                .predicate(StringUtils.isNotBlank(jack.getName()), (Specification<Phone>) (root, query, cb) -> {
                    Join address = root.join("addresses", JoinType.LEFT);
                    return cb.equal(address.get("street"), "Chengdu");
                })
                .build();


        List<Person> phones = personRepository.findAll(specification);

        // then
        assertThat(phones.size()).isEqualTo(2);
    }

}
