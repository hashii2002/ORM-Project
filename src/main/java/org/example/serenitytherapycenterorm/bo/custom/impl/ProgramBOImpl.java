package org.example.serenitytherapycenterorm.bo.custom.impl;

import org.example.serenitytherapycenterorm.bo.custom.ProgramBO;
import org.example.serenitytherapycenterorm.dao.DAOFactory;
import org.example.serenitytherapycenterorm.dao.custom.ProgramDAO;
import org.example.serenitytherapycenterorm.dao.custom.TherapistDAO;
import org.example.serenitytherapycenterorm.dto.TherapyProgramDTO;
import org.example.serenitytherapycenterorm.entity.Therapist;
import org.example.serenitytherapycenterorm.entity.TherapyProgram;
import org.example.serenitytherapycenterorm.exception.ValidationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ProgramBOImpl implements ProgramBO {

    private final ProgramDAO programDAO = (ProgramDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.PROGRAM);
    private final TherapistDAO therapistDAO = (TherapistDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.THERAPIST);

    private static final String NAME_PATTERN = "^[A-Za-z0-9|\\s\\-]{3,100}$";
    private static final String DURATION_PATTERN = "^[A-Za-z0-9|\\s]{2,30}$";
    private static final String SESSIONS_PATTERN = "^[1-9][0-9]{0,2}$";

    private void validateProgram(TherapyProgramDTO dto) throws ValidationException {
        if (dto.getName() == null || !dto.getName().matches(NAME_PATTERN)) {
            throw new ValidationException("Invalid Program Name! (Must be 3-100 characters)");
        }
        if (dto.getDuration() == null || !dto.getDuration().matches(DURATION_PATTERN)) {
            throw new ValidationException("Invalid Duration! (e.g., '12 weeks' or '6 months')");
        }
        if (dto.getTotalSessions() == null || !String.valueOf(dto.getTotalSessions()).matches(SESSIONS_PATTERN)) {
            throw new ValidationException("Invalid Total Sessions! (Must be a positive number)");
        }
        if (dto.getFee() == null || dto.getFee().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Invalid Program Fee! (Must be greater than 0.00)");
        }
    }

    @Override
    public boolean saveProgram(TherapyProgramDTO dto, String selectedTherapistName) throws Exception {
        validateProgram(dto);

        BigDecimal sessionFee = dto.getFee().divide(
                BigDecimal.valueOf(dto.getTotalSessions()), 2, java.math.RoundingMode.HALF_UP
        );

        TherapyProgram program = new TherapyProgram(
                null,
                dto.getName(),
                dto.getDuration(),
                dto.getFee(),
                dto.getTotalSessions(),
                sessionFee,
                dto.getDescription(),
                new ArrayList<>(), // Patients list
                new HashSet<>()    // Therapists set
        );

        if (selectedTherapistName != null && !selectedTherapistName.trim().isEmpty()) {
            Therapist therapist = therapistDAO.getTherapistByName(selectedTherapistName);
            if (therapist != null) {
                program.getTherapists().add(therapist);
                therapist.getPrograms().add(program);
            }
        }

        return programDAO.save(program);
    }

    @Override
    public boolean updateProgram(TherapyProgramDTO dto, String selectedTherapistName) throws Exception {
        validateProgram(dto);

        BigDecimal sessionFee = dto.getFee().divide(
                BigDecimal.valueOf(dto.getTotalSessions()), 2, java.math.RoundingMode.HALF_UP
        );

        TherapyProgram program = programDAO.search(dto.getId());
        if (program == null) return false;

        program.setName(dto.getName());
        program.setDuration(dto.getDuration());
        program.setFee(dto.getFee());
        program.setTotalSessions(dto.getTotalSessions());
        program.setSessionFee(sessionFee);
        program.setDescription(dto.getDescription());

        if (program.getTherapists() != null) {
            for (Therapist t : program.getTherapists()) {
                t.getPrograms().remove(program);
            }
            program.getTherapists().clear();
        }


        if (selectedTherapistName != null && !selectedTherapistName.trim().isEmpty()) {
            Therapist therapist = therapistDAO.getTherapistByName(selectedTherapistName);
            if (therapist != null) {
                program.getTherapists().add(therapist);
                therapist.getPrograms().add(program);
            }
        }

        return programDAO.update(program);
    }

    @Override
    public boolean deleteProgram(Long id) throws Exception {
        return programDAO.delete(id);
    }

    @Override
    public TherapyProgramDTO searchProgramById(Long id) throws Exception {
        TherapyProgram p = programDAO.search(id);
        if (p == null) return null;

        String tName = (p.getTherapists() != null && !p.getTherapists().isEmpty())
                ? p.getTherapists().iterator().next().getFullName() : null;

        return new TherapyProgramDTO(p.getId(), p.getName(), p.getDuration(), p.getFee(), p.getTotalSessions(), p.getDescription(), tName);
    }

    @Override
    public List<TherapyProgramDTO> searchProgramByName(String name) throws Exception {
        List<TherapyProgram> list = programDAO.searchByName(name);
        List<TherapyProgramDTO> dtoList = new ArrayList<>();
        for (TherapyProgram p : list) {
            String tName = (p.getTherapists() != null && !p.getTherapists().isEmpty())
                    ? p.getTherapists().iterator().next().getFullName() : null;

            dtoList.add(new TherapyProgramDTO(p.getId(), p.getName(), p.getDuration(), p.getFee(), p.getTotalSessions(), p.getDescription(), tName));
        }
        return dtoList;
    }

    @Override
    public List<TherapyProgramDTO> getAllPrograms() throws Exception {
        List<TherapyProgram> list = programDAO.getAll();
        List<TherapyProgramDTO> dtoList = new ArrayList<>();
        for (TherapyProgram p : list) {
            String tName = (p.getTherapists() != null && !p.getTherapists().isEmpty())
                    ? p.getTherapists().iterator().next().getFullName() : null;

            dtoList.add(new TherapyProgramDTO(p.getId(), p.getName(), p.getDuration(), p.getFee(), p.getTotalSessions(), p.getDescription(), tName));
        }
        return dtoList;
    }
}