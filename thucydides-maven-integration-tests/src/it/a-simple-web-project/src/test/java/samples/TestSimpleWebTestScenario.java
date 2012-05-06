package samples;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
public class TestSimpleWebTestScenario {

    @Managed(driver = "htmlunit", uniqueSession = true)
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:demosite/index.html")
    public Pages pages;
    
    @Steps
    public DemoSiteSteps demo_site;

    @Test
    public void select_a_value_in_a_dropdown() {
        demo_site.enter_values("Label 1", true);
        demo_site.should_have_selected_value("1");
    }


    @Test
    public void select_another_value_in_a_dropdown() {
        demo_site.enter_values("Label 3", true);
        demo_site.should_have_selected_value("3");
    }    


}
