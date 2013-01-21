package net.thucydides.core.steps

import net.thucydides.core.annotations.Step
import net.thucydides.core.pages.PageObject
import net.thucydides.core.pages.Pages
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenInstanciatingStepLibraries extends Specification {

    def driver = Mock(WebDriver)
    def pages = new Pages(driver)
    def stepFactory = new StepFactory(pages)

    static class MyPageObject extends PageObject {

        MyPageObject(WebDriver driver) {
            super(driver)
        }
    }

    static class MyOtherPageObject extends PageObject {

        MyOtherPageObject() {
            super(null)
        }
    }

    static MyOtherPageObject myOtherPageObject = new MyOtherPageObject()

    static class MyStepLibrary extends ScenarioSteps {

        MyPageObject myPageObject;
        MyOtherPageObject myInstantiatedPageObject;

        MyStepLibrary(Pages pages) {
            super(pages)
            myInstantiatedPageObject = myOtherPageObject
        }

        @Step
        def aStep() {}
    }

    def "the step factory should provide new instantiated step libraries"() {
        when:
            def myStepLibrary = stepFactory.getStepLibraryFor(MyStepLibrary)
        then:
            myStepLibrary != null
    }

    def "should instantiate any uninitialized page objects in a step class"() {
        when:
            def myStepLibrary = stepFactory.getStepLibraryFor(MyStepLibrary)
        then:
            myStepLibrary.myPageObject != null
    }

    def "should not instantiate page objects that are already instantiated"() {
        when:
            def myStepLibrary = stepFactory.getStepLibraryFor(MyStepLibrary)
        then:
            myStepLibrary.myInstantiatedPageObject == myOtherPageObject
    }
}
