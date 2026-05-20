package org.example.serenitytherapycenterorm.bo;

import org.example.serenitytherapycenterorm.bo.custom.impl.TherapistBOImpl;
import org.example.serenitytherapycenterorm.bo.custom.impl.UserBOImpl;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {}

    public static BOFactory getBoFactory() {
        return (boFactory == null) ? boFactory = new BOFactory() : boFactory;
    }

    public enum BOTypes {
        USER, THERAPIST, PROGRAM, PATIENT
    }

    public SuperBO getBO(BOTypes types) {
        switch (types) {
            case USER:
                return new UserBOImpl();
            case THERAPIST:
                return new TherapistBOImpl();
            default:
                return null;
        }
    }
}