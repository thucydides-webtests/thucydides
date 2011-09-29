package net.thucydides.junit.spring.samples.service;

import net.thucydides.junit.spring.samples.dao.UserDAO;
import net.thucydides.junit.spring.samples.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class UserService {

    private UserDAO userDAO;

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional
    public List<User> listUsers() {
        return userDAO.findAll();
    }

    @Transactional
    public void addNewUser(User newUser) {
        userDAO.save(newUser);
    }

}
