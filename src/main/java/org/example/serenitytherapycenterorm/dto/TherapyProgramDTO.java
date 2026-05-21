package org.example.serenitytherapycenterorm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TherapyProgramDTO {
    private Long id;
    private String name;
    private String duration;
    private BigDecimal fee;
    private Integer totalSessions;
    private String description;
    private String therapistName;
}