package com.codepresso.codepresso.repository.payment;

import com.codepresso.codepresso.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
