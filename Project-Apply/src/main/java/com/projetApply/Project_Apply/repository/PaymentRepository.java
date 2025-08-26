package com.projetApply.Project_Apply.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projetApply.Project_Apply.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
