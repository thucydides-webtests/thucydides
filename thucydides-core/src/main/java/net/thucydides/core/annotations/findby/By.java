package net.thucydides.core.annotations.findby;

import java.util.List;

import com.google.common.base.Preconditions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class By extends org.openqa.selenium.By {

    /**
     * @param scLocator The scLocator to use
     * @return a MyBy which locates elements via AutoTest
     */
    public static By sclocator(final String scLocator) {
        Preconditions.checkNotNull(scLocator);
        return new ByScLocator(scLocator);
    }

    public static class ByScLocator extends By {
        private final String scLocator;

        public ByScLocator(String scLocator) {
            this.scLocator = scLocator;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            throw new IllegalArgumentException(
                    "SmartGWT does not provide the functionality to find multiple elements");
        }

        @Override
        public WebElement findElement(SearchContext context) {
            WebElement element;
            try {
                element = (new WebDriverWait((WebDriver) context, 1))
                        .until(new ExpectedCondition<WebElement>() {
                            public WebElement apply(WebDriver driver) {
                                try {
                                    return (WebElement) ((JavascriptExecutor) driver)
                                            .executeScript("return AutoTest.getElement(arguments[0]);", scLocator);
                                } catch (WebDriverException e) {
                                    return null;
                                }
                            }
                        });
            } catch (TimeoutException e) {
                throw new NoSuchElementException("Cannot locate an element using "
                        + toString());

            }
            return element;
        }

        @Override
        public String toString() {
            return "By.sclocator: " + scLocator;
        }
    }

    /**
     * @param jQuerySelector The jquery to use
     * @return a By selector object which locates elements via jQuery
     */
    public static By jquery(final String jQuerySelector) {
        Preconditions.checkNotNull(jQuerySelector);
        return new ByjQuerySelector(jQuerySelector);
    }

    public static class ByjQuerySelector extends By {
        private final String jQuerySelector;

        public ByjQuerySelector(String jQuerySelector) {
            this.jQuerySelector = jQuerySelector;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            List<WebElement> elements;
            try {
                elements = (new WebDriverWait((WebDriver) context, 1))
                        .until(new ExpectedCondition<List<WebElement>>() {
                            @SuppressWarnings("unchecked")
                            public List<WebElement> apply(WebDriver driver) {
                                return (List<WebElement>) ((JavascriptExecutor) driver)
                                        .executeScript("var elements = $(arguments[0]).get(); return ((elements.length) ? elements : null)", jQuerySelector);
                            }
                        });
            } catch (TimeoutException e) {
                throw new NoSuchElementException("Cannot locate elements using " + toString());
            }
            return elements;

        }

        @Override
        public WebElement findElement(SearchContext context) {
            WebElement element;
            try {
                element = (new WebDriverWait((WebDriver) context, 1))
                        .until(new ExpectedCondition<WebElement>() {
                            public WebElement apply(WebDriver driver) {
                                return (WebElement) ((JavascriptExecutor) driver)
                                        .executeScript("return $(arguments[0]).get(0)", jQuerySelector);
                            }
                        });
            } catch (TimeoutException e) {
                throw new NoSuchElementException("Cannot locate an element using " + toString());
            }
            return element;
        }

        @Override
        public String toString() {
            return "By.jQuerySelector: " + jQuerySelector;
        }
    }


}
