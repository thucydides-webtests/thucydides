package net.thucydides.core.webdriver;

import java.lang.reflect.InvocationTargetException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * Centralize instantiation of WebDriver drivers.
 */
public class WebdriverInstanceFactory {

    public WebdriverInstanceFactory() {
    }

    public WebDriver newInstanceOf(final Class<? extends WebDriver> webdriverClass) throws IllegalAccessException, InstantiationException {
        return webdriverClass.newInstance();
    }

    public WebDriver newInstanceOf(final Class<? extends WebDriver> webdriverClass,
                                   final FirefoxProfile profile) throws IllegalAccessException,
            InstantiationException,
            NoSuchMethodException,
            InvocationTargetException {

        return webdriverClass.getConstructor(new Class[]{FirefoxProfile.class}).newInstance(profile);
    }
}