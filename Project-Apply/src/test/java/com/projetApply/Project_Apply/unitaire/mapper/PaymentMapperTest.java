package com.projetApply.Project_Apply.unitaire.mapper;

import com.projetApply.Project_Apply.dto.PaymentDTO;
import com.projetApply.Project_Apply.mapper.PaymentMapper;
import com.projetApply.Project_Apply.model.Payment;
import com.projetApply.Project_Apply.model.PaymentType;
import com.projetApply.Project_Apply.model.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentMapperTest {

    @Test
    void testToDTO_shouldMapCorrectly() {
        User employee = new User();
        employee.setId(42);

        Payment payment = new Payment();
        payment.setId(1);
        payment.setAmount(new BigDecimal("99.99"));
        payment.setType(PaymentType.CASH);
        payment.setPaymentDate(LocalDateTime.of(2025, 9, 12, 22, 0));
        payment.setEmployee(employee);

        PaymentDTO dto = PaymentMapper.toDTO(payment);

        assertEquals(1L, dto.getId());
        assertEquals(new BigDecimal("99.99"), dto.getAmount());
        assertEquals(PaymentType.CASH, dto.getType());
        assertEquals(LocalDateTime.of(2025, 9, 12, 22, 0), dto.getPaymentDate());
        assertEquals(42, dto.getEmployeeId());
    }

    @Test
    void testToEntity_shouldMapCorrectly() {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(2);
        dto.setAmount(new BigDecimal("49.50"));
        dto.setType(PaymentType.CARD);
        dto.setPaymentDate(LocalDateTime.of(2025, 9, 12, 23, 0));
        dto.setEmployeeId(99);

        User employee = new User();
        employee.setId(99);

        Payment payment = PaymentMapper.toEntity(dto, employee);

        assertEquals(2L, payment.getId());
        assertEquals(new BigDecimal("49.50"), payment.getAmount());
        assertEquals(PaymentType.CARD, payment.getType());
        assertEquals(LocalDateTime.of(2025, 9, 12, 23, 0), payment.getPaymentDate());
        assertEquals(99, payment.getEmployee().getId());
    }

    @Test
    void testClassInitialization() {
        assertNotNull(new PaymentMapper());
    }

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<PaymentMapper> constructor = PaymentMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
