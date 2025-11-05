package com.codepresso.codepresso.payment.repository;

import com.codepresso.codepresso.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
