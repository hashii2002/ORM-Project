package org.example.serenitytherapycenterorm.bo;

import org.example.serenitytherapycenterorm.bo.custom.impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {}

    public static BOFactory getBoFactory() {
        return (boFactory == null) ? boFactory = new BOFactory() : boFactory;
    }

    public enum BOTypes {
        USER, THERAPIST, PROGRAM, PATIENT, SESSION, PAYMENT
    }

    public SuperBO getBO(BOTypes types) {
        switch (types) {
            case USER:
                return new UserBOImpl();
            case THERAPIST:
                return new TherapistBOImpl();
            case PROGRAM:
                return new ProgramBOImpl();
            case PATIENT:
                return new PatientBOImpl();
            case SESSION:
                return new TherapySessionBOImpl();
            case PAYMENT:
                return new PaymentBOImpl();
            default:
                return null;
        }
    }
}