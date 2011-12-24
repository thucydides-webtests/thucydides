package net.thucydides.core.pages.jquery;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

public class WhenAddingJQuerySupportToPageObjects {

    MockEnvironmentVariables environmentVariables;

    @Mock
    WebDriver driver;

    class TestableJQueryEnabledPage extends JQueryEnabledPage {

        public List<String> executedScripts = new ArrayList<String>();

        TestableJQueryEnabledPage(WebDriver driver, EnvironmentVariables environmentVariables) {
            super(driver, environmentVariables);
        }

        @Override
        protected void executeScriptFrom(String scriptSource) {
            executedScripts.add(scriptSource);
        }
    }

    TestableJQueryEnabledPage page;

    @Before
    public void initMocks() {
        environmentVariables = new MockEnvironmentVariables();

        MockitoAnnotations.initMocks(this);

        page = new TestableJQueryEnabledPage(driver, environmentVariables);
    }

    @Test
    public void should_add_the_jquery_library_to_a_page() {

        page.injectJQuery();

        assertThat(page.executedScripts, hasItem(containsString("jquery.min.js")));
    }

    @Test
    public void should_not_add_the_highlighting_plugin_by_default() {
        page.injectJQueryPlugins();

        assertThat(page.executedScripts, not(hasItem(containsString("jquery-thucydides-plugin.js"))));
    }


    @Test
    public void should_add_the_highlighting_plugin_if_configured() {
        environmentVariables.setProperty("thucydides.activate.highlighting", "true");

        page.injectJQueryPlugins();

        assertThat(page.executedScripts, hasItem(containsString("jquery-thucydides-plugin.js")));
    }


}
