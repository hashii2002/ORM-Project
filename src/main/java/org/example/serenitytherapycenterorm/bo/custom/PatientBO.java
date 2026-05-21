package org.example.serenitytherapycenterorm.bo.custom;

import org.example.serenitytherapycenterorm.bo.SuperBO;
import org.example.serenitytherapycenterorm.dto.PatientDTO;
import java.util.List;

public interface PatientBO extends SuperBO {
    boolean savePatient(PatientDTO dto) throws Exception;
    List<PatientDTO> getAllPatients() throws Exception;
}
