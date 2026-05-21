package org.example.serenitytherapycenterorm.dao.custom.impl;

import org.example.serenitytherapycenterorm.config.FactoryConfiguration;
import org.example.serenitytherapycenterorm.dao.custom.PatientDAO;
import org.example.serenitytherapycenterorm.entity.Patient;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PatientDAOImpl implements PatientDAO {

    @Override
    public boolean save(Patient entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public List<Patient> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Patient> query = session.createQuery("FROM Patient", Patient.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    // සටහන: update, delete, search මෙතඩ්ස් ද මේ ආකාරයටම සම්පූර්ණ කරන්න...
    @Override public boolean update(Patient entity) throws Exception { return false; }
    @Override public boolean delete(Long id) throws Exception { return false; }
    @Override public Patient search(Long id) throws Exception { return null; }
}
