package com.projetApply.Project_Apply.mapper;

import com.projetApply.Project_Apply.dto.PaymentDTO;
import com.projetApply.Project_Apply.model.Payment;
import com.projetApply.Project_Apply.model.User;

public class PaymentMapper {

    public static PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setType(payment.getType());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setEmployeeId(payment.getEmployee().getId());
        return dto;
    }

    public static Payment toEntity(PaymentDTO dto, User employee) {
        Payment payment = new Payment();
        payment.setId(dto.getId());
        payment.setAmount(dto.getAmount());
        payment.setType(dto.getType());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setEmployee(employee);
        return payment;
    }

}
