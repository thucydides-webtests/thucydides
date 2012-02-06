package net.thucydides.core.webdriver.stubs;

import com.google.common.collect.Sets;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A description goes here.
 * User: johnsmart
 * Date: 6/02/12
 * Time: 10:37 AM
 */
public class OptionsStub implements WebDriver.Options {

    public OptionsStub() {
    }

    @Override
    public void addCookie(Cookie cookie) {
    }

    @Override
    public void deleteCookieNamed(String name) {
    }

    @Override
    public void deleteCookie(Cookie cookie) {
    }

    @Override
    public void deleteAllCookies() {
    }

    @Override
    public Set<Cookie> getCookies() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Cookie getCookieNamed(String name) {
        return new CookieStub(name,"");
    }



    @Override
    public WebDriver.Timeouts timeouts() {
        return new TimeoutsStub();
    }

    @Override
    public WebDriver.ImeHandler ime() {
        return new ImeHandlerStub();
    }



    @Override
    public WebDriver.Window window() {
        return new WindowStub();
    }


}
