package org.example.serenitytherapycenterorm.bo.custom.impl;

import org.example.serenitytherapycenterorm.Util.ValidationUtil;
import org.example.serenitytherapycenterorm.bo.custom.PatientBO;
import org.example.serenitytherapycenterorm.dao.custom.PatientDAO;
import org.example.serenitytherapycenterorm.dao.custom.impl.PatientDAOImpl;
import org.example.serenitytherapycenterorm.dto.PatientDTO;
import org.example.serenitytherapycenterorm.entity.Patient;
import org.example.serenitytherapycenterorm.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class PatientBOImpl implements PatientBO {

    private final PatientDAO patientDAO = new PatientDAOImpl();

    @Override
    public boolean savePatient(PatientDTO dto) throws Exception {
        // Validation
        if (!ValidationUtil.isValidName(dto.getName())) {
            throw new ValidationException("Invalid Patient Name! (3-100 characters, letters only)");
        }
        if (!ValidationUtil.isValidPhone(dto.getPhone())) {
            throw new ValidationException("Invalid Phone Number! (e.g., 0712345678)");
        }
        if (!ValidationUtil.isValidEmail(dto.getEmail())) {
            throw new ValidationException("Invalid Email Format! (e.g., name@mail.com)");
        }

        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        patient.setRegisteredDate(dto.getRegisteredDate());

        return patientDAO.save(patient);
    }

    @Override
    public List<PatientDTO> getAllPatients() throws Exception {
        List<Patient> allEntities = patientDAO.getAll();
        List<PatientDTO> allDTOs = new ArrayList<>();

        for (Patient p : allEntities) {
            allDTOs.add(new PatientDTO(
                    p.getId(),
                    p.getName(),
                    p.getEmail(),
                    p.getPhone(),
                    p.getAddress(),
                    p.getRegisteredDate(),
                    "Programs Info",
                    "No Notes",
                    "Active"
            ));
        }
        return allDTOs;
    }
}
