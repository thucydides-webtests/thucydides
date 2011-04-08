package net.thucydides.easyb.samples

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import net.thucydides.core.annotations.Step
import net.thucydides.core.pages.Pages
import net.thucydides.core.model.ScenarioSteps

class SampleSteps extends ScenarioSteps {

    SampleSteps(Pages pages){
        super(pages)
    }

    @Step
    def step1() {}

    @Step
    def step2() {}

    @Step
    def step3() {}

}
