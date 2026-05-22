package org.example.serenitytherapycenterorm.dao.custom;

import org.example.serenitytherapycenterorm.dao.CrudDAO;
import org.example.serenitytherapycenterorm.entity.User;
import java.util.List;

public interface UserDAO extends CrudDAO<User,Long> {
    User findByUsername(String username) throws Exception;
    List<User> searchByFullName(String name) throws Exception;
    long getUserCount() throws Exception;
}