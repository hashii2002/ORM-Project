package org.example.serenitytherapycenterorm.bo.custom;

import org.example.serenitytherapycenterorm.bo.SuperBO;
import org.example.serenitytherapycenterorm.dto.TherapyProgramDTO;
import java.util.List;

public interface ProgramBO extends SuperBO {
    boolean saveProgram(TherapyProgramDTO dto) throws Exception;
    boolean updateProgram(TherapyProgramDTO dto) throws Exception;
    boolean deleteProgram(Long id) throws Exception;
    TherapyProgramDTO searchProgramById(Long id) throws Exception;
    List<TherapyProgramDTO> searchProgramByName(String name) throws Exception;
    List<TherapyProgramDTO> getAllPrograms() throws Exception;
}