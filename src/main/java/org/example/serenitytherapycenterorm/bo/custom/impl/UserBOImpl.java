package org.example.serenitytherapycenterorm.bo.custom.impl;

import org.example.serenitytherapycenterorm.bo.custom.UserBO;
import org.example.serenitytherapycenterorm.dao.DAOFactory;
import org.example.serenitytherapycenterorm.dao.custom.UserDAO;
import org.example.serenitytherapycenterorm.dto.UserDTO;
import org.example.serenitytherapycenterorm.entity.User;
import org.example.serenitytherapycenterorm.exception.AuthenticationException;
import org.example.serenitytherapycenterorm.Util.PasswordUtil;

import java.util.ArrayList;
import java.util.List;

public class UserBOImpl implements UserBO {

    // Loose Coupling
    private final UserDAO userDAO = (UserDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.USER);

    @Override
    public boolean registerUser(UserDTO dto) throws Exception {
        String hashedPassword = PasswordUtil.hashPassword(dto.getPassword());

        User user = new User(
                null,
                dto.getUsername(),
                hashedPassword,
                dto.getFullName(),
                dto.getEmail(),
                dto.getAddress(),
                dto.getRole(),
                dto.getStatus()
        );
        return userDAO.save(user);
    }

    @Override
    public boolean updateUser(UserDTO dto) throws Exception {
        User user = new User(
                dto.getId(),
                dto.getUsername(),
                dto.getPassword(),
                dto.getFullName(),
                dto.getEmail(),
                dto.getAddress(),
                dto.getRole(),
                dto.getStatus()
        );
        return userDAO.update(user);
    }

    @Override
    public boolean deleteUser(Long id) throws Exception {
        return userDAO.delete(id);
    }

    @Override
    public List<UserDTO> getAllUsers() throws Exception {
        List<User> list = userDAO.getAll();
        List<UserDTO> dtoList = new ArrayList<>();
        for (User u : list) {
            dtoList.add(new UserDTO(
                    u.getId(), u.getUsername(), u.getPassword(),
                    u.getFullName(), u.getEmail(), u.getAddress(),
                    u.getRole(), u.getStatus()
            ));
        }
        return dtoList;
    }

    @Override
    public User authenticate(String username, String plainPassword) throws Exception {
        User user = userDAO.findByUsername(username);
        if (user == null || User.Status.INACTIVE.equals(user.getStatus())) {
            throw new org.example.serenitytherapycenterorm.exception.AuthenticationException("Invalid username or disabled account");
        }

        if (!PasswordUtil.checkPassword(plainPassword, user.getPassword())) {
            throw new org.example.serenitytherapycenterorm.exception.AuthenticationException("Invalid password! Please try again");
        }
        return user;
    }
}
