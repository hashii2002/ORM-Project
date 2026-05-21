package org.example.serenitytherapycenterorm.dto;

import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TherapySessionDTO {
    private Long sessionId;
    private Long patientId;
    private String patientName;
    private Long programId;
    private String programName;
    private Long therapistId;
    private String therapistName;
    private LocalDate sessionDate;
    private String timeSlot;
    private String status;

    public String getDateString() {
        return (sessionDate != null) ? sessionDate.toString() : "";
    }
}