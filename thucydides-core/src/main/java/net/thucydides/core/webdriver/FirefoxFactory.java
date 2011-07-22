package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class FirefoxFactory {
    public WebDriver newUntrustedCertificateCompatibleDriver() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setAssumeUntrustedCertificateIssuer(true);
        return new FirefoxDriver(profile);
    }
}
