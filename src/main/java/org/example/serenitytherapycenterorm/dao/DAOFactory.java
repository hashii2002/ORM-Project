package org.example.serenitytherapycenterorm.dao;

import org.example.serenitytherapycenterorm.dao.custom.impl.*;

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
            case THERAPIST:
                return new TherapistDAOImpl();
            case PROGRAM:
                return new ProgramDAOImpl();
            case PATIENT:
                return new PatientDAOImpl();
            case SESSION:
                return new TherapySessionDAOImpl();
            default:
                return null;
        }
    }
}
