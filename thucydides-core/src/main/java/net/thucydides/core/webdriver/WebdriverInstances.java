package net.thucydides.core.webdriver;

import net.thucydides.core.ThucydidesSystemProperty;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * One or more WebDriver drivers that are being used in a test.
 */
public class WebdriverInstances {

    private final Map<String, WebDriver> driverMap;

    private String currentDriver;

    public WebdriverInstances() {
        this.driverMap = new HashMap<String, WebDriver>();
    }

    public WebDriver getCurrentDriver() {
        if (driverMap.containsKey(currentDriver)) {
            return driverMap.get(currentDriver);
        } else {
            return null;
        }
    }

    public String getCurrentDriverName() {
        return currentDriver;
    }

    public void closeCurrentDriver() {
        if (getCurrentDriver() != null) {
            WebDriver driver = getCurrentDriver();
            closeAndQuite(driver);
            driverMap.remove(currentDriver);
            currentDriver  = null;
        }
    }

    private void closeAndQuite(WebDriver driver) {
        driver.close();
        driver.quit();
    }

    public void resetCurrentDriver() {
        if (getCurrentDriver() != null) {
            WebDriver driver = getCurrentDriver();
            if (WebDriverFacade.class.isAssignableFrom(driver.getClass())) {
                ((WebDriverFacade) driver).reset();
            }
        }

    }

    public boolean driverIsRegisteredFor(String driverName) {
        return driverMap.containsKey(normalized(driverName));
    }

    public WebDriver useDriver(final String driverName) {
        this.currentDriver = normalized(driverName);
        return driverMap.get(currentDriver);
    }

    public void closeAllDrivers() {
        Collection<WebDriver> openDrivers = driverMap.values();
        for(WebDriver driver : openDrivers) {
            closeAndQuite(driver);
        }
        driverMap.clear();
        currentDriver = null;
    }

    public final class InstanceRegistration {
        private final String driverName;

        public InstanceRegistration(final String driverName) {
            this.driverName = normalized(driverName);
        }


        public void forDriver(final WebDriver driver) {
            driverMap.put(normalized(driverName), driver);
            currentDriver = normalized(driverName);
        }
    }

    public InstanceRegistration registerDriverCalled(final String driverName) {
        return new InstanceRegistration(normalized(driverName));
    }

    private String normalized(String name) {
        if (StringUtils.isEmpty(name)) {
            return WebDriverFactory.DEFAULT_DRIVER;
        } else {
            return name.toLowerCase();
        }
    }
}
