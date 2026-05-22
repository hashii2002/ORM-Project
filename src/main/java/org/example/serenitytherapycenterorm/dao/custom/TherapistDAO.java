package org.example.serenitytherapycenterorm.dao.custom;

import org.example.serenitytherapycenterorm.dao.CrudDAO;
import org.example.serenitytherapycenterorm.entity.Therapist;
import java.util.List;

public interface TherapistDAO extends CrudDAO<Therapist, Long> {
    List<Therapist> searchByFullName(String name) throws Exception;
    Therapist getTherapistByName(String name) throws Exception;
    long getTherapistCount() throws Exception;
}