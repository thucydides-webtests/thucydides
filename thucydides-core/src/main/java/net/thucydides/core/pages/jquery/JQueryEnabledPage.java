package net.thucydides.core.pages.jquery;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import net.thucydides.core.webdriver.javascript.JavaScriptExecutorFacade;
import org.openqa.selenium.WebDriver;

import java.net.URL;

import static net.thucydides.core.webdriver.javascript.JavascriptSupport.javascriptIsSupportedIn;

public class JQueryEnabledPage {

    private final WebDriver driver;

    public JQueryEnabledPage(WebDriver driver) {
        this.driver = driver;
    }

    public static JQueryEnabledPage withDriver(final WebDriver driver) {
        return new JQueryEnabledPage(driver);
    }

    public boolean isJQueryEnabled() {
        if (javascriptIsSupportedIn(driver)) {
            JavaScriptExecutorFacade js = new JavaScriptExecutorFacade(driver);
            Boolean result = (Boolean) js.executeScript("return (typeof jQuery === 'function')");
            return ((result != null) && (result));
        }
        return false;
    }


    public void injectJQuery() {
        executeScriptFrom("jquery/jquery.min.js");
    }

    private void executeScriptFrom(String scriptSource) {
        if (javascriptIsSupportedIn(driver)) {
            String script = getFileAsString(scriptSource);
            JavaScriptExecutorFacade js = new JavaScriptExecutorFacade(driver);
            js.executeScript(script);
        }
    }

    private String getFileAsString(final String resourcePath) {
        String content = "";
        try {
            URL fileUrl = getClass().getClassLoader().getResource(resourcePath);
            content = Resources.toString(fileUrl, Charsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return content;
    }

    public void injectJQueryPlugins() {
        executeScriptFrom("jquery/jquery-thucydides-plugin.js");
    }
}
