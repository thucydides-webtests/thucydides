package net.thucydides.core.pages.jquery;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import net.thucydides.core.pages.JavaScriptExecutorFacade;
import org.openqa.selenium.WebDriver;

import java.net.URL;

public class JQueryEnabledPage {

    private final WebDriver driver;

    public JQueryEnabledPage(WebDriver driver) {
        this.driver = driver;
    }

    public static JQueryEnabledPage withDriver(final WebDriver driver) {
        return new JQueryEnabledPage(driver);
    }

    public boolean containsJQuery() {
        JavaScriptExecutorFacade js = new JavaScriptExecutorFacade(driver);
        return (Boolean) js.executeScript("return (typeof jQuery === 'function')");
    }

    public void injectJQuery() {
        String jquery = getFileAsString("jquery/jquery.min.js");
        JavaScriptExecutorFacade js = new JavaScriptExecutorFacade(driver);
        js.executeScript(jquery);
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

    public static boolean scriptContainsJQuery(final String script) {
         return (script.contains("$(") || script.toLowerCase().contains("jquery("));

    }
}
