package org.example.serenitytherapycenterorm.bo.custom.impl;

import org.example.serenitytherapycenterorm.bo.custom.AdminDashboardHomeBO;
import org.example.serenitytherapycenterorm.dao.DAOFactory;
import org.example.serenitytherapycenterorm.dao.custom.PaymentDAO;
import org.example.serenitytherapycenterorm.dao.custom.ProgramDAO;
import org.example.serenitytherapycenterorm.dao.custom.TherapistDAO;
import org.example.serenitytherapycenterorm.dao.custom.UserDAO;

public class AdminDashboardHomeBOImpl implements AdminDashboardHomeBO {
    private final UserDAO userDAO = (UserDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.USER);
    private final TherapistDAO therapistDAO = (TherapistDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.THERAPIST);
    private final ProgramDAO programDAO = (ProgramDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.PROGRAM);
    private final PaymentDAO paymentDAO = (PaymentDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.PAYMENT);

    @Override
    public long getTotalUsers() throws Exception {
        return userDAO.getUserCount();
    }

    @Override
    public long getTotalTherapists() throws Exception {
        return therapistDAO.getTherapistCount();
    }

    @Override
    public long getTotalPrograms() throws Exception {
        return programDAO.getProgramCount();
    }

    @Override
    public double getTotalRevenue() throws Exception {
        return paymentDAO.getTotalRevenue();
    }


}
