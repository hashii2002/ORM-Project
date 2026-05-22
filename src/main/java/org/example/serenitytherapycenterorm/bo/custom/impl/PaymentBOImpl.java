package org.example.serenitytherapycenterorm.bo.custom.impl;

import org.example.serenitytherapycenterorm.bo.custom.PaymentBO;
import org.example.serenitytherapycenterorm.dao.DAOFactory;
import org.example.serenitytherapycenterorm.dao.custom.PatientDAO;
import org.example.serenitytherapycenterorm.dao.custom.PaymentDAO;
import org.example.serenitytherapycenterorm.dto.PaymentDTO;
import org.example.serenitytherapycenterorm.entity.Patient;
import org.example.serenitytherapycenterorm.entity.Payment;
import java.util.ArrayList;
import java.util.List;

public class PaymentBOImpl implements PaymentBO {

    private final PaymentDAO paymentDAO = (PaymentDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.PAYMENT);
    private final PatientDAO patientDAO = (PatientDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.PATIENT);

    @Override
    public boolean savePayment(PaymentDTO dto) throws Exception {
        Patient patient = patientDAO.search(dto.getPatientId());
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found!");
        }

        Payment payment = new Payment();
        payment.setPatient(patient);
        payment.setTotalFee(dto.getTotalFee());
        payment.setUpfrontAmount(dto.getUpfrontAmount());
        payment.setAmountPaid(dto.getAmountPaid());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMethod(dto.getPaymentMethod());

        return paymentDAO.save(payment);
    }

    @Override
    public boolean deletePayment(Long id) throws Exception {
        return paymentDAO.delete(id);
    }

    @Override
    public List<PaymentDTO> getAllPayments() throws Exception {
        List<Payment> allEntities = paymentDAO.getAll();
        List<PaymentDTO> allDTOs = new ArrayList<>();

        for (Payment p : allEntities) {
            allDTOs.add(new PaymentDTO(
                    p.getPaymentId(),
                    p.getPatient().getId(),
                    p.getPatient().getName(),
                    p.getTotalFee(),
                    p.getUpfrontAmount(),
                    p.getAmountPaid(),
                    p.getPaymentDate(),
                    p.getPaymentMethod(),
                    p.getStatus()
            ));
        }
        return allDTOs;
    }

    @Override
    public boolean updatePayment(PaymentDTO dto) throws Exception {
        Payment payment = paymentDAO.search(dto.getPaymentId());
        if (payment == null) {
            throw new IllegalArgumentException("Payment record not found!");
        }

        Patient patient = patientDAO.search(dto.getPatientId());
        payment.setPatient(patient);

        payment.setTotalFee(dto.getTotalFee());
        payment.setUpfrontAmount(dto.getUpfrontAmount());
        payment.setAmountPaid(dto.getAmountPaid());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(dto.getStatus());

        return paymentDAO.update(payment);
    }
}
