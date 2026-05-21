package org.example.serenitytherapycenterorm.dto;

import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class PaymentDTO {
    private Long paymentId;
    private Long patientId;
    private String patientName;
    private Double totalFee;
    private Double upfrontAmount;
    private Double amountPaid;
    private LocalDate paymentDate;
    private String paymentMethod;
}