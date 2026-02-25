package org.example.cinema.repository;

import org.example.cinema.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // findByUserId - поиск платежей пользователя
    // вход: userId - идентификатор пользователя
    // выход: список платежей пользователя
    List<Payment> findByUserId(Long userId);

    // findByStatus - поиск платежей по статусу
    // вход: status - статус платежа
    // выход: список платежей с указанным статусом
    List<Payment> findByStatus(String status);

    // findByPaymentDateBetween - поиск платежей по диапазону дат
    // вход:
    //   - startDate - начальная дата
    //   - endDate - конечная дата
    // выход: список платежей, совершенных в указанный период
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // findByTransactionId - поиск платежа по номеру транзакции
    // вход: transactionId - номер транзакции
    // выход: Optional<Payment> - платеж, если найден
    Optional<Payment> findByTransactionId(String transactionId);

    // calculateTotalRevenueByPeriod - расчет выручки за период
    // вход:
    //   - startDate - начальная дата
    //   - endDate - конечная дата
    // выход: общая сумма платежей за период
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' AND p.paymentDate BETWEEN :startDate AND :endDate")
    Double calculateTotalRevenueByPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // findPaymentStatsByMethod - получение статистики по методам оплаты
    // вход: startDate - начальная дата
    // выход: список статистики по методам оплаты
    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount), AVG(p.amount) " +
            "FROM Payment p WHERE p.status = 'SUCCESS' AND p.paymentDate >= :startDate " +
            "GROUP BY p.paymentMethod")
    List<Object[]> findPaymentStatsByMethod(@Param("startDate") LocalDateTime startDate);
}