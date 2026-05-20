package org.example.serenitytherapycenterorm.dao.custom.impl;

import org.example.serenitytherapycenterorm.config.FactoryConfiguration;
import org.example.serenitytherapycenterorm.dao.custom.ProgramDAO;
import org.example.serenitytherapycenterorm.entity.TherapyProgram;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ProgramDAOImpl implements ProgramDAO {

    @Override
    public boolean save(TherapyProgram entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        session.persist(entity);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean update(TherapyProgram entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        session.update(entity);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        TherapyProgram program = session.get(TherapyProgram.class, id);
        if (program != null) {
            session.remove(program);
            transaction.commit();
            session.close();
            return true;
        }
        transaction.rollback();
        session.close();
        return false;
    }

    @Override
    public TherapyProgram search(Long id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        TherapyProgram program = session.get(TherapyProgram.class, id);
        session.close();
        return program;
    }

    @Override
    public List<TherapyProgram> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Query<TherapyProgram> query = session.createQuery("FROM TherapyProgram", TherapyProgram.class);
        List<TherapyProgram> list = query.list();
        session.close();
        return list;
    }

    @Override
    public List<TherapyProgram> searchByName(String name) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Query<TherapyProgram> query = session.createQuery("FROM TherapyProgram WHERE name LIKE :name", TherapyProgram.class);
        query.setParameter("name", "%" + name + "%");
        List<TherapyProgram> list = query.list();
        session.close();
        return list;
    }
}