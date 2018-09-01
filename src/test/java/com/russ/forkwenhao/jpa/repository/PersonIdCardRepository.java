package com.russ.forkwenhao.jpa.repository;

import com.russ.forkwenhao.jpa.model.PersonIdCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonIdCardRepository extends JpaRepository<PersonIdCard, Long>, JpaSpecificationExecutor<PersonIdCard> {
}
