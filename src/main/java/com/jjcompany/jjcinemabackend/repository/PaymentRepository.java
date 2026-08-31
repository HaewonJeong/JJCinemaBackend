package com.jjcompany.jjcinemabackend.repository;

import com.jjcompany.jjcinemabackend.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBookingByBookingId(Long bookingId);
}
