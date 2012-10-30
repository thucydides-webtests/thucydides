package net.thucydides.junit.pipeline;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

public class SomeSteps extends ScenarioSteps {

        public SomeSteps(Pages pages) {
            super(pages)
        }

        @Step
        public void step1(){println "step1"}

        @Step
        public void step2(){println "step2"}

        @Step
        public void step3(){println "step3"}

    }