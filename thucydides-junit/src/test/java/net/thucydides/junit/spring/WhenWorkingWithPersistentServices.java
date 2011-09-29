package net.thucydides.junit.spring;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.junit.spring.samples.dao.UserDAO;
import net.thucydides.junit.spring.samples.domain.User;
import net.thucydides.junit.spring.samples.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(ThucydidesRunner.class)
@ContextConfiguration(locations = "/spring/db-config.xml")
@TransactionConfiguration
public class WhenWorkingWithPersistentServices {

    @Managed
    WebDriver driver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;

    @Rule
    public SpringIntegration springIntegration = SpringIntegration.forClass(this.getClass());

    @Autowired
    public UserService userService;

    @Before
    public void setupTestData() {
        userService.addNewUser(new User("Jake", "secret", "USA"));
        userService.addNewUser(new User("Jill", "secret", "USA"));
    }

    @Test
    public void testsShouldHaveTestDataAvailable() {
        List<User> users = userService.listUsers();
        assertThat(users.size(), is(2));
    }
}