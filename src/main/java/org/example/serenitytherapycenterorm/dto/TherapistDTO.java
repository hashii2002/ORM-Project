package org.example.serenitytherapycenterorm.dto;

import lombok.*;
import org.example.serenitytherapycenterorm.entity.Therapist;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TherapistDTO {
    private Long id;
    private String fullName;
    private Therapist.Specialty specialty;
    private String phone;
    private String email;
    private Therapist.Status status;
}