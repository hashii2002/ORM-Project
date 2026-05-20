package org.example.serenitytherapycenterorm.dao.custom.impl;

import org.example.serenitytherapycenterorm.config.FactoryConfiguration;
import org.example.serenitytherapycenterorm.dao.custom.UserDAO;
import org.example.serenitytherapycenterorm.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public boolean save(User entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(User entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean delete(Long id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // 💡 මුලින්ම අදාළ ID එක තියෙන Object එක Load කරගෙන පසුව Delete කරයි
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public User search(Long id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(User.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<User> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            // HQL (Hibernate Query Language) මඟින් සියලුම දත්ත ලබා ගැනීම
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    // 💡 UserDAO එකට විතරක් අයිති Custom මෙතඩ් එක ලියන ආකාරය
    @Override
    public User findByUsername(String username) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult(); // එක සමාන Username එකක් පමණක් බලාපොරොත්තු වන නිසා
        } finally {
            session.close();
        }
    }
}
