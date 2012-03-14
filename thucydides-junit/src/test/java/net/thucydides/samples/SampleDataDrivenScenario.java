package net.thucydides.samples;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.TestData;
import net.thucydides.junit.runners.ThucydidesParameterizedRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Collection;

@RunWith(ThucydidesParameterizedRunner.class)
public class SampleDataDrivenScenario {


    @TestData
    public static Collection testData() {
            return Arrays.asList(new Object[][]{
                    {"a", 1},
                    {"b", 2},
                    {"c", 3},
                    {"d", 4},
                    {"e", 5},
                    {"f", 6},
                    {"g", 7},
                    {"h", 8},
                    {"i", 9},
                    {"j", 10}
            });
        }
    private String option1;
    private Integer option2;

    public SampleDataDrivenScenario(String option1, Integer option2) {
        this.option1 = option1;
        this.option2 = option2;
    }

    @Managed(driver="htmlunit")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;

    @Steps
    public SampleScenarioSteps steps;
        
    @Test
    public void happy_day_scenario() {
        steps.stepWithParameters(option1,option2);
    }

}
