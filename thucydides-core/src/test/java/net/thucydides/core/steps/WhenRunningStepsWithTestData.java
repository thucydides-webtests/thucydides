package net.thucydides.core.steps;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.csv.FailedToInitializeTestData;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

import static net.thucydides.core.steps.StepData.setDefaultStepFactory;
import static net.thucydides.core.steps.StepData.withTestDataFrom;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class WhenRunningStepsWithTestData {


    @Mock
    WebDriver driver;

    @Mock
    StepListener listener;

    BaseStepListener baseStepListener;

    private StepFactory factory;

    static class TestSteps extends ScenarioSteps {

        private String name;
        private String address;
        private String dateOfBirth;

        public TestSteps(Pages pages) {
            super(pages);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        @StepGroup
        public void step_group1() {
            step1();
            step2();
        }

        @StepGroup
        public void name_and_dob() {
            step1();
            step3();
        }


        @Step
        public TestSteps step1() {
            getDriver().get(name);
            return this;
        }

        @Step
        public TestSteps step2() {
            getDriver().get(address);
            return this;
        }

        @Step
        public TestSteps step3() {
            getDriver().get(dateOfBirth);
            return this;
        }

        @Step
        public void fail_sometimes() {
            getDriver().get(name);
            if (name.equals("Joe")) {
                throw new AssertionError("Bad name");
            }
        }

    }


    static class DifferentTestSteps extends ScenarioSteps {

        private String name;
        private String address;

        public DifferentTestSteps(Pages pages) {
            super(pages);
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Step
        public void nameStep() {
            getDriver().get(name);
        }

        @Step
        public void addressStep() {
            getDriver().get(address);
        }
    }


    public static class TestStepsWithNoSetters extends ScenarioSteps {

        public String name;
        public String address;

        public TestStepsWithNoSetters(Pages pages) {
            super(pages);
        }

        @Step
        public void nameStep() {
            getDriver().get(name);
        }

        @Step
        public void addressStep() {
            getDriver().get(address);
        }
    }

    public static class TestStepsWithNoSettersAndInaccessibleFields extends ScenarioSteps {

        private String name;
        private String address;

        public TestStepsWithNoSettersAndInaccessibleFields(Pages pages) {
            super(pages);
        }

        @Step
        public void nameStep() {
            getDriver().get(name);
        }

        @Step
        public void addressStep() {
            getDriver().get(address);
        }
    }

    @Rule
    public ExtendedTemporaryFolder temporaryFolder = new ExtendedTemporaryFolder();
    private File outputDirectory;

    @Before
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        factory = new StepFactory(new Pages(driver));
        outputDirectory = temporaryFolder.newFolder();
        baseStepListener = new BaseStepListener(outputDirectory);

        StepEventBus.getEventBus().clear();
        StepEventBus.getEventBus().registerListener(listener);
        StepEventBus.getEventBus().registerListener(baseStepListener);

        setDefaultStepFactory(null);
    }

    @After
    public void finishTest() {
        StepEventBus.getEventBus().clear();
        StepEventBus.getEventBus().dropAllListeners();
    }

    @Test
    public void should_invoke_step_method_for_each_row_in_the_csv_file() throws IOException {

        TestSteps steps = factory.getStepLibraryFor(TestSteps.class);

        StepEventBus.getEventBus().testStarted("data driven test");
        withTestDataFrom("testdata/test.csv").usingFactory(factory).run(steps).step1();

        verify(driver, times(3)).get(anyString());
    }


    @Test
    public void should_record_each_row_as_a_step() throws IOException {

        TestSteps steps = factory.getStepLibraryFor(TestSteps.class);

        StepEventBus.getEventBus().testStarted("data driven test");
        withTestDataFrom("testdata/test.csv").usingFactory(factory).run(steps).step1();
        StepEventBus.getEventBus().testFinished();
        TestOutcome outcome = baseStepListener.getTestOutcomes().get(0);
        assertThat(outcome.getStepCount()).isEqualTo(3);
    }

    @Test
    public void should_record_each_row_executed_in_a_step_as_a_nested_step() throws IOException {

        TestSteps steps = factory.getStepLibraryFor(TestSteps.class);

        StepEventBus.getEventBus().testStarted("data driven test");
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("a step"));
        withTestDataFrom("testdata/test.csv").usingFactory(factory).run(steps).step1();
        StepEventBus.getEventBus().stepFinished();
        StepEventBus.getEventBus().testFinished();

        TestOutcome outcome = baseStepListener.getTestOutcomes().get(0);
        assertThat(outcome.getStepCount()).isEqualTo(1);
        assertThat(outcome.getTestSteps().get(0).getChildren()).hasSize(3);


    }
    @Test
    public void should_pass_test_data_into_invoked_methods() throws IOException {

        TestSteps steps = factory.getStepLibraryFor(TestSteps.class);

        StepEventBus.getEventBus().testStarted("data driven test");
        withTestDataFrom("testdata/test.csv").usingFactory(factory).run(steps).step1();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_execute_all_tests_even_if_one_of_the_steps_fails() throws IOException {

        TestSteps steps = factory.getStepLibraryFor(TestSteps.class);

        StepEventBus.getEventBus().testStarted("data driven test");
        withTestDataFrom("testdata/test.csv").usingFactory(factory).run(steps).fail_sometimes();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_notify_listeners_if_one_of_the_steps_fails() throws IOException {

        TestSteps steps = factory.getStepLibraryFor(TestSteps.class);

        StepEventBus.getEventBus().testStarted("data driven test");
        withTestDataFrom("testdata/test.csv")
                .usingFactory(factory)
                .run(steps).fail_sometimes();

        verify(listener).stepFailed(any(StepFailure.class));
    }


    @Test
    public void should_be_able_to_define_a_default_factory_before_the_step() throws IOException {

        TestSteps steps = factory.getStepLibraryFor(TestSteps.class);

        setDefaultStepFactory(factory);

        StepEventBus.getEventBus().testStarted("data driven test");
        withTestDataFrom("testdata/test.csv")
                .run(steps).step1();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_be_able_to_define_a_default_test_step_class_before_the_step() throws IOException {

        TestSteps steps = factory.getStepLibraryFor(TestSteps.class);

        StepEventBus.getEventBus().testStarted("data driven test");
        withTestDataFrom("testdata/test.csv").usingFactory(factory).run(steps).step1();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_be_able_to_define_default_factory_and_test_step_class_before_the_step() throws IOException {

        TestSteps steps = factory.getStepLibraryFor(TestSteps.class);

        setDefaultStepFactory(factory);

        StepEventBus.getEventBus().testStarted("data driven test");
        withTestDataFrom("testdata/test.csv").run(steps).step1();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_be_able_to_use_test_data_with_semicolons() throws IOException {

        TestSteps steps =  factory.getStepLibraryFor(TestSteps.class);

        setDefaultStepFactory(factory);

        StepEventBus.getEventBus().testStarted("data driven test");
        withTestDataFrom("testdata/semicolon-test.csv").separatedBy(';').run(steps).step1();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_be_able_to_use_different_step_libraries_in_the_same_test() throws IOException {

        TestSteps steps =  factory.getStepLibraryFor(TestSteps.class);
        DifferentTestSteps differentSteps = factory.getStepLibraryFor(DifferentTestSteps.class);

        setDefaultStepFactory(factory);

        StepEventBus.getEventBus().testStarted("data driven test");
        withTestDataFrom("testdata/test.csv").run(steps).name_and_dob();

        withTestDataFrom("testdata/test.csv").run(differentSteps).nameStep();

        verify(driver, times(2)).get("Bill");
        verify(driver, times(1)).get("10/10/1970");
    }


    @Test
    public void should_be_able_to_use_a_step_library_with_public_fields_and_no_setters() throws IOException {
        TestStepsWithNoSetters steps = factory.getStepLibraryFor(TestStepsWithNoSetters.class);

        setDefaultStepFactory(factory);

        StepEventBus.getEventBus().testStarted("data driven test");
        withTestDataFrom("testdata/test.csv").run(steps).nameStep();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_work_with_private_fields() throws IOException {
        TestStepsWithNoSettersAndInaccessibleFields steps
                = factory.getStepLibraryFor(TestStepsWithNoSettersAndInaccessibleFields.class);

        StepEventBus.getEventBus().testStarted("data driven test");
        setDefaultStepFactory(factory);

        withTestDataFrom("testdata/test.csv").run(steps).nameStep();
    }

}
