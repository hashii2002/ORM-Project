package org.example.serenitytherapycenterorm.bo.custom.impl;

import org.example.serenitytherapycenterorm.bo.custom.TherapistBO;
import org.example.serenitytherapycenterorm.dao.DAOFactory;
import org.example.serenitytherapycenterorm.dao.custom.TherapistDAO;
import org.example.serenitytherapycenterorm.dto.TherapistDTO;
import org.example.serenitytherapycenterorm.entity.Therapist;
import org.example.serenitytherapycenterorm.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TherapistBOImpl implements TherapistBO {

    private final TherapistDAO therapistDAO = (TherapistDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.THERAPIST);

    private static final String NAME_PATTERN = "^[A-z|\\s]{3,}$";
    private static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PHONE_PATTERN = "^0[0-9]{9}$";

    // Regex Validations
    private void validateTherapist(TherapistDTO dto) throws ValidationException {

        if (dto.getFullName() == null || !dto.getFullName().matches(NAME_PATTERN)) {
            throw new ValidationException("Invalid Name! (Must be at least 3 letters long without special characters)");
        }

        if (dto.getSpecialty() == null) {
            throw new ValidationException("Please select a valid specialty!");
        }

        if (dto.getPhone() == null || !dto.getPhone().matches(PHONE_PATTERN)) {
            throw new ValidationException("Invalid Phone number! (Must be 10 digits starting with 0, e.g., 0771234567)");
        }

        if (dto.getEmail() == null || !dto.getEmail().matches(EMAIL_PATTERN)) {
            throw new ValidationException("Invalid Email Address! (e.g., example@mail.com)");
        }

        if (dto.getStatus() == null) {
            throw new ValidationException("Please select a valid status!");
        }
    }

    @Override
    public boolean saveTherapist(TherapistDTO dto) throws Exception {
        validateTherapist(dto);
        return therapistDAO.save(new Therapist(
                dto.getId(),
                dto.getFullName(),
                dto.getSpecialty(),
                dto.getPhone(),
                dto.getEmail(),
                dto.getStatus(),
                new HashSet<>(),
                new HashSet<>()
        ));
    }

    @Override
    public boolean updateTherapist(TherapistDTO dto) throws Exception {
        validateTherapist(dto);
        return therapistDAO.update(new Therapist(
                dto.getId(),
                dto.getFullName(),
                dto.getSpecialty(),
                dto.getPhone(),
                dto.getEmail(),
                dto.getStatus(),
                new HashSet<>(),
                new HashSet<>()
        ));
    }

    @Override
    public boolean deleteTherapist(Long id) throws Exception {
        return therapistDAO.delete(id);
    }

    @Override
    public TherapistDTO searchTherapistById(Long id) throws Exception {
        Therapist t = therapistDAO.search(id);
        if (t == null) return null;
        return new TherapistDTO(t.getId(), t.getFullName(), t.getSpecialty(), t.getPhone(), t.getEmail(), t.getStatus());
    }

    @Override
    public List<TherapistDTO> searchTherapistByFullName(String name) throws Exception {
        List<Therapist> list = therapistDAO.searchByFullName(name);
        List<TherapistDTO> dtoList = new ArrayList<>();
        for (Therapist t : list) {
            dtoList.add(new TherapistDTO(t.getId(), t.getFullName(), t.getSpecialty(), t.getPhone(), t.getEmail(), t.getStatus()));
        }
        return dtoList;
    }

    @Override
    public List<TherapistDTO> getAllTherapists() throws Exception {
        List<Therapist> list = therapistDAO.getAll();
        List<TherapistDTO> dtoList = new ArrayList<>();
        for (Therapist t : list) {
            dtoList.add(new TherapistDTO(t.getId(), t.getFullName(), t.getSpecialty(), t.getPhone(), t.getEmail(), t.getStatus()));
        }
        return dtoList;
    }
}