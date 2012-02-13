package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.Logs;

import java.util.Collections;
import java.util.Set;

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

    @Override
    public Logs logs() {
        return new Logs(){
            @Override
            public LogEntries get(String s) {
                return null;
            }
        };
    }


}
