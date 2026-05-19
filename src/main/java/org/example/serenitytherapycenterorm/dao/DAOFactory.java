package org.example.serenitytherapycenterorm.dao;

import org.example.serenitytherapycenterorm.dao.custom.impl.UserDAOImpl;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {
    }

    public static DAOFactory getDaoFactory() {
        return (daoFactory == null) ? daoFactory = new DAOFactory() : daoFactory;
    }

    public enum DAOTypes {
        USER, THERAPIST, PROGRAM, PATIENT, PAYMENT, SESSION
    }

    public SuperDAO getDAO(DAOTypes types) {
        switch (types) {
            case USER:
                return new UserDAOImpl();
            // පසුව සාදන DAO Implementations මෙතැනට එකතු කරන්න:
            // case THERAPIST:
            //     return new TherapistDAOImpl();
            default:
                return null;
        }
    }
}
