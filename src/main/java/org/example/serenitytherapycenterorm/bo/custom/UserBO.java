package org.example.serenitytherapycenterorm.bo.custom;

import org.example.serenitytherapycenterorm.bo.SuperBO;
import org.example.serenitytherapycenterorm.dto.UserDTO;
import org.example.serenitytherapycenterorm.entity.User;

import java.util.List;

public interface UserBO extends SuperBO {
    boolean registerUser(UserDTO dto) throws Exception;
    boolean updateUser(UserDTO dto) throws Exception;
    boolean deleteUser(Long id) throws Exception;
    List<UserDTO> getAllUsers() throws Exception;
    User authenticate(String username, String plainPassword) throws Exception;
}
