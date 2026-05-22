package org.example.serenitytherapycenterorm.bo.custom;

import org.example.serenitytherapycenterorm.bo.SuperBO;
import org.example.serenitytherapycenterorm.dto.PaymentDTO;
import java.util.List;

public interface PaymentBO extends SuperBO {
    boolean savePayment(PaymentDTO dto) throws Exception;
    boolean deletePayment(Long id) throws Exception;
    List<PaymentDTO> getAllPayments() throws Exception;
    boolean updatePayment(PaymentDTO dto) throws Exception;
}
