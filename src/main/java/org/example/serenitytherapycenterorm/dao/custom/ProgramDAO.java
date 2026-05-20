package org.example.serenitytherapycenterorm.dao.custom;

import org.example.serenitytherapycenterorm.dao.CrudDAO;
import org.example.serenitytherapycenterorm.entity.TherapyProgram;
import java.util.List;

public interface ProgramDAO extends CrudDAO<TherapyProgram, Long> {
    List<TherapyProgram> searchByName(String name) throws Exception;
}
