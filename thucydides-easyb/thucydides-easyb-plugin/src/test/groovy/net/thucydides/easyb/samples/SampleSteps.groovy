package net.thucydides.easyb.samples

import net.thucydides.core.annotations.Pending
import net.thucydides.core.annotations.Step
import net.thucydides.core.annotations.Steps
import net.thucydides.core.pages.Pages
import net.thucydides.core.steps.ScenarioSteps
import org.junit.Ignore

class SampleSteps extends ScenarioSteps {

    @Steps
    MoreSampleSteps moreSampleSteps;

    SampleSteps(Pages pages){
        super(pages)
    }

    @Step
    def step1() {}

    @Step
    def step2() {}

    @Step
    def step3() {}

    @Step
    def nestedSteps() {
        moreSampleSteps.step1()
        moreSampleSteps.step2()
        moreSampleSteps.step3()

    }

    @Step
    def failingStep() {
        assert 1 == 2
    }

    @Ignore
    @Step
    def ignoredStep() {
    }

    @Pending
    @Step
    def pendingStep() {

    }
}
