package net.thucydides.samples;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Qualifier;
import net.thucydides.junit.annotations.UseTestDataFrom;
import net.thucydides.junit.runners.ThucydidesParameterizedRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesParameterizedRunner.class)
@UseTestDataFrom(value="test-data/simple-data.csv")
//@BatchSize(10)
public class SampleCSVDataDrivenScenario {

    private String name;
    private String age;
    private String address;

    public SampleCSVDataDrivenScenario() {
    }

    @Qualifier
    public String getQualifier() {
        return name;
    }

    @Managed(driver="htmlunit")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;

    @Steps
    public SampleScenarioSteps steps;

    @Before
    public void setup() {
    }

    @After
    public void teardown() {
    }

    @Test
    public void data_driven_test() {
        System.out.println(getName() + "/" + getAge() + "/" + getAddress());
    }

    @Test
    public void another_data_driven_test() {
        System.out.println(getName() + "/" + getAge() + "/" + getAddress());
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
