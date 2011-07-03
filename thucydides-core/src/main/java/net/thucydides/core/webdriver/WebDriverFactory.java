package net.thucydides.core.webdriver;

import net.thucydides.core.pages.WebElementFacade;
import org.omg.CORBA.TIMEOUT;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocator;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.DefaultElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import javax.xml.transform.Templates;
import java.lang.reflect.Field;
import java.sql.Driver;
import java.util.Arrays;
import java.util.List;

/**
 * Provides an instance of a supported WebDriver.
 * When you instanciate a Webdriver instance for Firefox or Chrome, it opens a new browser.
 * We
 * 
 * @author johnsmart
 *
 */
public class WebDriverFactory {

    /***
     * Create a new WebDriver instance of a given type.
     */
    public WebDriver newInstanceOf(final SupportedWebDriver driverType)  {
        if (driverType == null) {
            throw new IllegalArgumentException("Driver type cannot be null");
        }

        return newWebdriverInstance(driverType.getWebdriverClass());
    }

    public static Class<? extends WebDriver> getClassFor(final SupportedWebDriver driverType)  {
        return driverType.getWebdriverClass();
    }

    protected WebDriver newWebdriverInstance(Class<? extends WebDriver> webdriverClass) {
        try {
            return webdriverClass.newInstance();
        } catch (Exception cause) {
            throw new UnsupportedDriverException("Could not instantiate " + webdriverClass, cause);
        }
    }


    static class DisplayedElementLocator extends AjaxElementLocator {

        private static final List<String> QUICK_METHODS = Arrays.asList("isCurrentlyVisible");
        private static final List<String> QUICK_CLASSES = Arrays.asList(WebElementFacade.class.getName());

        private final Field field;
        private final WebDriver driver;

        DisplayedElementLocator(WebDriver driver, Field field, int timeOutInSeconds) {
            super(driver, field, timeOutInSeconds);
            this.field = field;
            this.driver = driver;
        }

        @Override
        public WebElement findElement() {
            if (shouldFindElementImmediately()) {
                return findElementImmediately();
            } else {
                return super.findElement();
            }
        }

        private boolean shouldFindElementImmediately() {
            for(StackTraceElement elt : Thread.currentThread().getStackTrace()){
                if (QUICK_METHODS.contains(elt.getMethodName())
                    &&  QUICK_CLASSES.contains(elt.getClassName())) {
                    return true;
                }
            }
            return false;
        }

        public WebElement findElementImmediately() {
            Annotations annotations = new Annotations(field);
            By by = annotations.buildBy();
            return driver.findElement(by);
        }

        @Override
        protected boolean isElementUsable(WebElement element) {
            return element.isDisplayed();
        }
    }

    static class DisplayedElementLocatorFactory extends AjaxElementLocatorFactory {
        private final WebDriver driver;
        private final int timeOutInSeconds;
        public DisplayedElementLocatorFactory(WebDriver driver, int timeOutInSeconds) {
            super(driver, timeOutInSeconds);
            this.driver = driver;
            this.timeOutInSeconds = timeOutInSeconds;
        }

        @Override
        public ElementLocator createLocator(Field field) {
            return new DisplayedElementLocator(driver, field, timeOutInSeconds);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }
    /**
     * Initialize a page object's fields using the specified WebDriver instance.
     */
    public static void initElementsWithAjaxSupport(final Object pageObject, final WebDriver driver) {
        ElementLocatorFactory finder = new DisplayedElementLocatorFactory(driver, Configuration.getElementTimeout());
        PageFactory.initElements(finder, pageObject);

    }

    /**
     * For data-driven tests, it can be useful to restart some browsers (e.g. Firefox) periodically.
     */
    public void restartBrowser() {
    }
}
