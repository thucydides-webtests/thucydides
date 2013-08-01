package net.thucydides.core.pages.jquery;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import org.openqa.selenium.WebDriver;

import java.net.URL;

import static net.thucydides.core.webdriver.javascript.JavascriptSupport.javascriptIsSupportedIn;

public class JQueryEnabledPage {

    private final WebDriver driver;
    private final EnvironmentVariables environmentVariables;

    protected JQueryEnabledPage(WebDriver driver) {
        this(driver, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    protected JQueryEnabledPage(WebDriver driver, EnvironmentVariables environmentVariables) {
        this.driver = driver;
        this.environmentVariables = environmentVariables;

    }

    public static JQueryEnabledPage withDriver(final WebDriver driver) {
        return new JQueryEnabledPage(driver);
    }

    public boolean isJQueryEnabled() {
        boolean jqueryIntegrationEnabled =
                    Boolean.valueOf(ThucydidesSystemProperty.JQUERY_INTEGRATION
                                                            .from(environmentVariables,"true"));

        if (jqueryIntegrationEnabled && javascriptIsSupportedIn(driver)) {
            JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
            Boolean result = (Boolean) js.executeScript("return (typeof jQuery === 'function')");
            return ((result != null) && (result));
        }
        return false;
    }


    public void injectJQuery() {
        executeScriptFrom("jquery/jquery.min.js");
        executeScriptFrom("jquery/jquery.focus.test-fix.js");
    }

    protected void executeScriptFrom(String scriptSource) {
        if (javascriptIsSupportedIn(driver)) {
            String script = getFileAsString(scriptSource);
            JavascriptExecutorFacade js = new JavascriptExecutorFacade(driver);
            js.executeScript(script);
        }
    }

    private String getFileAsString(final String resourcePath) {
        String content = "";
        try {
            URL fileUrl = getClass().getClassLoader().getResource(resourcePath);
            content = Resources.toString(fileUrl, Charsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    public void injectJQueryPlugins() {
        if (environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.ACTIVTE_HIGHLIGHTING.getPropertyName(), false)) {
            executeScriptFrom("jquery/jquery-thucydides-plugin.js");
        }
    }
    
    public void injectJavaScriptUtils(){
    	executeScriptFrom("javascript/cycle.js");
    }
}
