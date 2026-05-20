package org.example.serenitytherapycenterorm.bo.custom;

import org.example.serenitytherapycenterorm.bo.SuperBO;
import org.example.serenitytherapycenterorm.dto.TherapistDTO;
import java.util.List;

public interface TherapistBO extends SuperBO {
    boolean saveTherapist(TherapistDTO dto) throws Exception;
    boolean updateTherapist(TherapistDTO dto) throws Exception;
    boolean deleteTherapist(Long id) throws Exception;
    TherapistDTO searchTherapistById(Long id) throws Exception;
    List<TherapistDTO> searchTherapistByFullName(String name) throws Exception;
    List<TherapistDTO> getAllTherapists() throws Exception;
}
