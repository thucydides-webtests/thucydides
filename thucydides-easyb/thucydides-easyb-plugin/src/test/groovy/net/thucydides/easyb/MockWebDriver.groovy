package net.thucydides.easyb

import org.openqa.selenium.WebElement
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver.TargetLocator
import org.openqa.selenium.WebDriver.Navigation
import org.openqa.selenium.WebDriver.Options
import org.openqa.selenium.WebDriver

class MockWebDriver implements WebDriver {

    def openedUrls = []
    def closed = false;

    void get(String url) {
        openedUrls << url
    }

    String getCurrentUrl() {}

    String getTitle() {}

    List<WebElement> findElements(By by) {
        return null
    }

    WebElement findElement(By by) {
        return null
    }

    String getPageSource() {
        return null
    }

    Set<String> getWindowHandles() {
        return null
    }

    String getWindowHandle() {
        return null
    }

    TargetLocator switchTo() {
        return null
    }

    Navigation navigate() {
        return null
    }

    Options manage() {
        return null
    }

    void close() {
        closed = true;
    }

    void quit() {

    }


    def boolean openedAt(String url) {
        openedUrls.contains(url)
    }

    def boolean wasClosed() {
        closed
    }

}
