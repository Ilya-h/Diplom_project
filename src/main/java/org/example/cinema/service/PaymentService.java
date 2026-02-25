package org.example.cinema.service;

import org.example.cinema.model.Payment;
import org.example.cinema.model.User;
import org.example.cinema.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // createPayment - создание платежа
    public Payment createPayment(User user, String paymentMethod, Double amount) {
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PENDING");
        payment.setTransactionId(generateTransactionId());

        return paymentRepository.save(payment);
    }

    // confirmPayment - подтверждение платежа
    public Payment confirmPayment(Long paymentId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setStatus("SUCCESS");
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found");
    }

    // refundPayment - возврат платежа
    public Payment refundPayment(Long paymentId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setStatus("REFUNDED");
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found");
    }

    // failPayment - отказ платежа
    public Payment failPayment(Long paymentId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setStatus("FAILED");
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found");
    }

    // getPaymentById - получение платежа по ID
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    // getPaymentsByUser - получение платежей пользователя
    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    // getTotalRevenue - получение общей выручки
    public Double getTotalRevenue() {
        return paymentRepository.calculateTotalRevenueByPeriod(
                LocalDateTime.now().minusYears(1), LocalDateTime.now());
    }

    // generateTransactionId - генерация ID транзакции
    private String generateTransactionId() {
        return "TRX" + System.currentTimeMillis() + new java.util.Random().nextInt(1000);
    }
}