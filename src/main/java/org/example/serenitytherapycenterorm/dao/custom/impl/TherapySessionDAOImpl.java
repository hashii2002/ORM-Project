package org.example.serenitytherapycenterorm.dao.custom.impl;

import org.example.serenitytherapycenterorm.config.FactoryConfiguration;
import org.example.serenitytherapycenterorm.dao.custom.TherapySessionDAO;
import org.example.serenitytherapycenterorm.entity.TherapySession;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class TherapySessionDAOImpl implements TherapySessionDAO {

    @Override
    public boolean save(TherapySession entity) throws Exception {
        Session session =  FactoryConfiguration.getInstance().getSession();
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
    public boolean update(TherapySession entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(entity);
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
    public boolean delete(Long id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            TherapySession entity = session.get(TherapySession.class, id);
            if (entity != null) {
                session.remove(entity);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public TherapySession search(Long id) throws Exception {
        Session session =  FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(TherapySession.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<TherapySession> getAll() throws Exception {
        Session session =  FactoryConfiguration.getInstance().getSession();
        try {
            Query<TherapySession> query = session.createQuery("FROM TherapySession", TherapySession.class);
            return query.list();
        } finally {
            session.close();
        }
    }
}
