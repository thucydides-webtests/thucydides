package net.thucydides.core.webdriver;

import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.AjaxElementLocator;
import org.openqa.selenium.support.pagefactory.Annotations;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

class DisplayedElementLocator extends AjaxElementLocator {

    private static final List<String> QUICK_METHODS = Arrays.asList("isCurrentlyVisible", "isCurrentlyEnabled",
                                                                    "waitUntilVisible", "waitUntilEnabled");
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
