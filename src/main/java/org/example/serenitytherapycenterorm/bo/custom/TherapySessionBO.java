package org.example.serenitytherapycenterorm.bo.custom;

import org.example.serenitytherapycenterorm.bo.SuperBO;
import org.example.serenitytherapycenterorm.dto.TherapySessionDTO;
import java.util.List;

public interface TherapySessionBO extends SuperBO {
    boolean scheduleSession(TherapySessionDTO dto) throws Exception;
    boolean cancelSession(Long sessionId) throws Exception;
    List<TherapySessionDTO> getAllSessions() throws Exception;
}
