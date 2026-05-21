package org.example.serenitytherapycenterorm.dto;

import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class PatientDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate registeredDate;
    private String programsDisplay;
    private String interviewNote;
    private String status;
}
