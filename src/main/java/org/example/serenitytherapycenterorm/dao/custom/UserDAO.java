package org.example.serenitytherapycenterorm.dao.custom;

import org.example.serenitytherapycenterorm.dao.CrudDAO;
import org.example.serenitytherapycenterorm.dao.SuperDAO;
import org.example.serenitytherapycenterorm.entity.User;
import java.util.List;

public interface UserDAO extends CrudDAO<User,Long> {
//    boolean save(User entity) throws Exception;
//    boolean update(User entity) throws Exception;
//    boolean delete(Long id) throws Exception;
    User findByUsername(String username) throws Exception;
//    List<User> getAll() throws Exception;
}