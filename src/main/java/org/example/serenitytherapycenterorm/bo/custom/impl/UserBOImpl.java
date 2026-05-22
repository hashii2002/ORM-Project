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

    private final UserDAO userDAO = (UserDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.USER);

    @Override
    public boolean saveUser(UserDTO dto) throws Exception {
        String defaultPassword = "123456";

        String hashedPassword = PasswordUtil.hashPassword(defaultPassword);

        User user = new User(
                null, // Auto-generated ID
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
    public UserDTO authenticate(String username, String plainPassword) throws Exception {
        User user = userDAO.findByUsername(username);

        if (user == null || !user.getUsername().equals(username)) {
            throw new AuthenticationException("Invalid username! Please check capital/simple letters.");
        }

        if (User.Status.INACTIVE.equals(user.getStatus())) {
            throw new AuthenticationException("Your account has been disabled!");
        }

        if (!PasswordUtil.checkPassword(plainPassword, user.getPassword())) {
            throw new AuthenticationException("Invalid password! Please try again");
        }

        return new UserDTO(
                user.getId(), user.getUsername(), user.getPassword(),
                user.getFullName(), user.getEmail(), user.getAddress(),
                user.getRole(), user.getStatus()
        );
    }

    @Override
    public UserDTO searchUserById(Long id) throws Exception {
        User user = userDAO.search(id);
        if (user == null) return null;

        return new UserDTO(
                user.getId(), user.getUsername(), user.getPassword(),
                user.getFullName(), user.getEmail(), user.getAddress(),
                user.getRole(), user.getStatus()
        );
    }

    @Override
    public List<UserDTO> searchUserByFullName(String name) throws Exception {

        List<User> list = userDAO.searchByFullName(name);
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
}
