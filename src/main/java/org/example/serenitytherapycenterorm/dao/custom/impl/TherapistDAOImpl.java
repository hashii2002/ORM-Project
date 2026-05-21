package org.example.serenitytherapycenterorm.dao.custom.impl;

import org.example.serenitytherapycenterorm.config.FactoryConfiguration;
import org.example.serenitytherapycenterorm.dao.custom.TherapistDAO;
import org.example.serenitytherapycenterorm.entity.Therapist;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class TherapistDAOImpl implements TherapistDAO {

    @Override
    public boolean save(Therapist entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        session.persist(entity);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean update(Therapist entity) throws Exception {
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
        Therapist therapist = session.get(Therapist.class, id);
        if (therapist != null) {
            session.remove(therapist);
            transaction.commit();
            session.close();
            return true;
        }
        transaction.rollback();
        session.close();
        return false;
    }

    @Override
    public Therapist search(Long id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Therapist therapist = session.get(Therapist.class, id);
        session.close();
        return therapist;
    }

    @Override
    public List<Therapist> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Query<Therapist> query = session.createQuery("FROM Therapist", Therapist.class);
        List<Therapist> list = query.list();
        session.close();
        return list;
    }

    @Override
    public List<Therapist> searchByFullName(String name) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Query<Therapist> query = session.createQuery("FROM Therapist WHERE LOWER(fullName) LIKE :name", Therapist.class);
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        List<Therapist> list = query.list();
        session.close();
        return list;
    }

    @Override
    public Therapist getTherapistByName(String name) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.createQuery("FROM Therapist WHERE fullName = :name", Therapist.class)
                    .setParameter("name", name)
                    .uniqueResult();
        } finally {
            session.close();
        }
    }
}
