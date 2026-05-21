package org.example.serenitytherapycenterorm.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
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
    private LocalDate dob;
    private List<TherapyProgramDTO> programs;

    public PatientDTO(Long id, String name, String email, String phone, String address,
                      LocalDate registeredDate, String programsDisplay, String interviewNote,
                      String status, LocalDate dob) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registeredDate = registeredDate;
        this.programsDisplay = programsDisplay;
        this.interviewNote = interviewNote;
        this.status = status;
        this.dob = dob;
    }

    public String getDobString() {
        return (dob != null) ? dob.toString() : "";
    }
}