package net.thucydides.core.webdriver;

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
            driver.close();
            driver.quit();
            driverMap.remove(currentDriver);
            currentDriver  = null;
        }
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
        return driverMap.containsKey(driverName);
    }

    public WebDriver useDriver(final String driverName) {
        this.currentDriver = driverName;
        return driverMap.get(driverName);
    }

    public void closeAllDrivers() {
        Collection<WebDriver> openDrivers = driverMap.values();
        for(WebDriver driver : openDrivers) {
            driver.close();
            driver.quit();
        }
        driverMap.clear();
        currentDriver = null;
    }

    public final class InstanceRegistration {
        private final String driverName;

        public InstanceRegistration(final String driverName) {
            this.driverName = driverName;
        }


        public void forDriver(final WebDriver driver) {
            driverMap.put(driverName, driver);
            currentDriver = driverName;
        }
    }

    public InstanceRegistration registerDriverCalled(final String driverName) {
        return new InstanceRegistration(driverName);
    }
}
