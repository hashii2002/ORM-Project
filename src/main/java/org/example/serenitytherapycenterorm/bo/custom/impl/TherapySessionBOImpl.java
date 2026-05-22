package org.example.serenitytherapycenterorm.bo.custom.impl;

import org.example.serenitytherapycenterorm.bo.custom.TherapySessionBO;
import org.example.serenitytherapycenterorm.config.FactoryConfiguration;
import org.example.serenitytherapycenterorm.dao.DAOFactory;
import org.example.serenitytherapycenterorm.dao.custom.TherapySessionDAO;
import org.example.serenitytherapycenterorm.dto.TherapySessionDTO;
import org.example.serenitytherapycenterorm.entity.Patient;
import org.example.serenitytherapycenterorm.entity.Therapist;
import org.example.serenitytherapycenterorm.entity.TherapyProgram;
import org.example.serenitytherapycenterorm.entity.TherapySession;
import org.example.serenitytherapycenterorm.exception.ValidationException;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TherapySessionBOImpl implements TherapySessionBO {

    private final TherapySessionDAO sessionDAO = (TherapySessionDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.SESSION);

    @Override
    public boolean scheduleSession(TherapySessionDTO dto) throws Exception {
        // Validation
        if (dto.getPatientId() == null) throw new ValidationException("Please select a valid patient!");
        if (dto.getProgramId() == null) throw new ValidationException("Please select a therapy program!");
        if (dto.getTherapistId() == null) throw new ValidationException("Please select a therapist!");
        if (dto.getSessionDate() == null || dto.getSessionDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Session date must be today or a future date!");
        }

        if (dto.getTimeSlot() == null || !dto.getTimeSlot().matches("^[0-9]{2}:[0-9]{2}\\s*(AM|PM)\\s*-\\s*[0-9]{2}:[0-9]{2}\\s*(AM|PM)$")) {
            throw new ValidationException("Invalid Time Slot selection standard!");
        }

        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Patient patient = session.get(Patient.class, dto.getPatientId());
            TherapyProgram program = session.get(TherapyProgram.class, dto.getProgramId());
            Therapist therapist = session.get(Therapist.class, dto.getTherapistId());

            TherapySession therapySession = new TherapySession();
            if (dto.getSessionId() != null) {
                therapySession.setSessionId(dto.getSessionId());
            }
            therapySession.setPatient(patient);
            therapySession.setProgram(program);
            therapySession.setTherapist(therapist);
            therapySession.setSessionDate(dto.getSessionDate());
            therapySession.setTimeSlot(dto.getTimeSlot());
            therapySession.setStatus(dto.getStatus());

            if (dto.getSessionId() == null) {
                return sessionDAO.save(therapySession);
            } else {
                return sessionDAO.update(therapySession);
            }
        } finally {
            session.close();
        }
    }

    @Override
    public boolean cancelSession(Long sessionId) throws Exception {
        TherapySession entity = sessionDAO.search(sessionId);
        if (entity != null) {
            entity.setStatus("Cancelled");
            return sessionDAO.update(entity);
        }
        return false;
    }

    @Override
    public List<TherapySessionDTO> getAllSessions() throws Exception {
        List<TherapySession> list = sessionDAO.getAll();
        List<TherapySessionDTO> dtoList = new ArrayList<>();
        for (TherapySession s : list) {
            dtoList.add(new TherapySessionDTO(
                    s.getSessionId(),
                    s.getPatient().getId(),
                    s.getPatient().getName(),
                    s.getProgram().getId(),
                    s.getProgram().getName(),
                    s.getTherapist().getId(),
                    s.getTherapist().getFullName(),
                    s.getSessionDate(),
                    s.getTimeSlot(),
                    s.getStatus()
            ));
        }
        return dtoList;
    }

    // Summary Card Logic

    @Override
    public long getTotalSessionCount() throws Exception {
        try (org.hibernate.Session session = org.example.serenitytherapycenterorm.config.FactoryConfiguration.getInstance().getSession()) {
            return (long) session.createQuery("SELECT COUNT(s.sessionId) FROM TherapySession s").uniqueResult();
        }
    }

    @Override
    public long getConfirmedAppointmentCount() throws Exception {
        try (org.hibernate.Session session = org.example.serenitytherapycenterorm.config.FactoryConfiguration.getInstance().getSession()) {
            return (long) session.createQuery("SELECT COUNT(s.sessionId) FROM TherapySession s WHERE s.status = :status")
                    .setParameter("status", "Confirmed")
                    .uniqueResult();
        }
    }
}