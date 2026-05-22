package org.example.serenitytherapycenterorm.bo.custom;

import org.example.serenitytherapycenterorm.bo.SuperBO;

public interface AdminDashboardHomeBO extends SuperBO {
    long getTotalUsers() throws Exception;
    long getTotalTherapists() throws Exception;
    long getTotalPrograms() throws Exception;
    double getTotalRevenue() throws Exception;
}
