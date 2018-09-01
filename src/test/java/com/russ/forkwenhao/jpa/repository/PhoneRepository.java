package com.russ.forkwenhao.jpa.repository;

import com.russ.forkwenhao.jpa.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PhoneRepository extends JpaRepository<Phone, Long>, JpaSpecificationExecutor<Phone> {
}
