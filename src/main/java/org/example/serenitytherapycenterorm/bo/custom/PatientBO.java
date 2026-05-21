package org.example.serenitytherapycenterorm.bo.custom;

import org.example.serenitytherapycenterorm.dto.PatientDTO;
import java.util.List;

public interface PatientBO {
    boolean savePatient(PatientDTO dto) throws Exception;
    List<PatientDTO> getAllPatients() throws Exception;
}
