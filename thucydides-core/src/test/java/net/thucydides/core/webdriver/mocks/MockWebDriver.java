package net.thucydides.core.webdriver.mocks;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MockWebDriver implements WebDriver {

    private List<String> openedUrls = new ArrayList<String>();
    private boolean closed = false;
    private int closedCount = 0;
    private int getCount = 0;

    public void get(String url) {
        openedUrls.add(0, url);
    }

    public String getCurrentUrl() {
        return null;
    }

    public String getTitle() {
        return null;
    }

    public List<WebElement> findElements(By by) {
        return null;
    }

    public WebElement findElement(By by) {
        return null;
    }

    public String getPageSource() {
        return null;
    }

    public Set<String> getWindowHandles() {
        return null;
    }

    public String getWindowHandle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public TargetLocator switchTo() {
        return null;
    }

    public Navigation navigate() {
        return null;
    }

    public Options manage() {
        return null;
    }

    public void close() {
        closed = true;
        closedCount++;
    }

    public int getClosedCount() {
        return closedCount;
    }

    public void quit() {
    }


    public void shouldHaveOpenedAt(String url) {
        if (!openedUrls.contains(url)) {
            throw new AssertionError("The URL " + url + " was never opened: opened URLS were: " + openedUrls);
        }

    }

    public boolean wasClosed() {
        return closed;
    }

    public <X> X getScreenshotAs(OutputType<X> target) {
        return null;
    }
}
