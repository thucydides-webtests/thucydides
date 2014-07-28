package net.thucydides.core.model.samples;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;

public class MyInheritedStepLibrary extends MyBaseStepLibrary {


    MyInheritedStepLibrary(Pages pages) {
        super(pages);
    }

    @Step
    public boolean anotherStep() {
        aProtectedMethod();
        return aProtectedStep();
    }
}