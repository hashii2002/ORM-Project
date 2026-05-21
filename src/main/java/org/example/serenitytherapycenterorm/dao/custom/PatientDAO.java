package org.example.serenitytherapycenterorm.dao.custom;

import org.example.serenitytherapycenterorm.dao.CrudDAO;
import org.example.serenitytherapycenterorm.entity.Patient;

import java.util.List;

public interface PatientDAO extends CrudDAO<Patient, Long> {
    List<Patient> getPatientsWithPrograms() throws Exception;
    List<Patient> getPatientsEnrolledInAllPrograms() throws Exception;
}

