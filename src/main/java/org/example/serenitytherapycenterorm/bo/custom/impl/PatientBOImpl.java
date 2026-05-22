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
        // 1. Validation
        if (!ValidationUtil.isValidName(dto.getName())) {
            throw new ValidationException("Invalid Patient Name!");
        }
        if (!ValidationUtil.isValidPhone(dto.getPhone())) {
            throw new ValidationException("Invalid Phone Number!");
        }

        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        patient.setRegisteredDate(dto.getRegisteredDate());
        patient.setDob(dto.getDob());
        patient.setInterviewNote(dto.getInterviewNote());
        patient.setStatus(dto.getStatus());

        List<org.example.serenitytherapycenterorm.entity.TherapyProgram> programEntities = new ArrayList<>();

        if (dto.getPrograms() != null) {

            org.hibernate.Session session = org.example.serenitytherapycenterorm.config.FactoryConfiguration.getInstance().getSession();

            try {
                for (org.example.serenitytherapycenterorm.dto.TherapyProgramDTO pDTO : dto.getPrograms()) {
                    org.example.serenitytherapycenterorm.entity.TherapyProgram existingProgram =
                            session.get(org.example.serenitytherapycenterorm.entity.TherapyProgram.class, pDTO.getId());

                    if (existingProgram != null) {
                        programEntities.add(existingProgram);
                    }
                }
            } finally {
                session.close();
            }
        }

        patient.setPrograms(programEntities);

        return patientDAO.save(patient);
    }

    @Override
    public List<PatientDTO> getAllPatients() throws Exception {
        List<Patient> allEntities = patientDAO.getAll();
        List<PatientDTO> allDTOs = new ArrayList<>();

        for (Patient p : allEntities) {

            StringBuilder programsBuilder = new StringBuilder();
            if (p.getPrograms() != null && !p.getPrograms().isEmpty()) {
                for (org.example.serenitytherapycenterorm.entity.TherapyProgram program : p.getPrograms()) {
                    if (programsBuilder.length() > 0) {
                        programsBuilder.append(", ");
                    }
                    programsBuilder.append(program.getName());
                }
            } else {
                programsBuilder.append("No Programs");
            }

            allDTOs.add(new PatientDTO(
                    p.getId(),
                    p.getName(),
                    p.getEmail(),
                    p.getPhone(),
                    p.getAddress(),
                    p.getRegisteredDate(),
                    programsBuilder.toString(),
                    p.getInterviewNote() != null ? p.getInterviewNote() : "No Notes",
                    p.getStatus() != null ? p.getStatus() : "Active",
                    p.getDob()
            ));
        }
        return allDTOs;
    }

    // Summary Card
    @Override
    public long getPatientCount() throws Exception {
        try (org.hibernate.Session session = org.example.serenitytherapycenterorm.config.FactoryConfiguration.getInstance().getSession()) {
            return (long) session.createQuery("SELECT COUNT(p.id) FROM Patient p").uniqueResult();
        }
    }
}