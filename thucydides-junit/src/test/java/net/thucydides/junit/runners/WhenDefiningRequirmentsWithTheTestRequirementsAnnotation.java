package net.thucydides.junit.runners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

import java.io.File;
import java.util.List;
import java.util.Set;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestStep;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.junit.samples.SingleTestScenario;
import net.thucydides.junit.samples.SingleTestScenarioWithSeveralBusinessRules;
import net.thucydides.junit.samples.SuccessfulSingleTestScenario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.RunNotifier;

public class WhenDefiningRequirmentsWithTheTestRequirementsAnnotation extends AbstractTestStepRunnerTest {


    TestableWebDriverFactory webDriverFactory;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Before
    public void initMocks() {
        File temporaryDirectory = tempFolder.newFolder("screenshots");
        webDriverFactory = new TestableWebDriverFactory(temporaryDirectory);
    }
    
    @Test
    public void the_TestsRequirement_annotation_can_associated_a_business_rule_to_a_test() throws Exception {
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        
        assertThat(testRun.getTestedRequirements(), hasItem("SOME_BUSINESS_RULE"));

    }    
    
    @Test
    public void the_TestsRequirement_annotation_can_associated_several_business_rules_to_a_test() throws Exception {
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenarioWithSeveralBusinessRules.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        
        assertThat(testRun.getTestedRequirements(), hasItem("SOME_BUSINESS_RULE_1"));
        assertThat(testRun.getTestedRequirements(), hasItem("SOME_BUSINESS_RULE_2"));

    }    

    
    @Test
    public void the_TestsRequirement_annotation_can_associated_a_business_rule_to_a_test_step() throws Exception {
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        List<TestStep> steps = testRun.getTestSteps();
        ConcreteTestStep step1 = (ConcreteTestStep) steps.get(0);
        
        assertThat(step1.getTestedRequirements(), hasItem("LOW_LEVEL_BUSINESS_RULE"));

    }    

    @Test
    public void the_TestsRequirement_annotation_can_associated_multiple_business_rules_to_a_test_step() throws Exception {
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();        
        AcceptanceTestRun testRun = executedScenarios.get(0);
        List<TestStep> steps = testRun.getTestSteps();
        ConcreteTestStep step2 = (ConcreteTestStep) steps.get(3);
        
        assertThat(step2.getTestedRequirements(), hasItem("LOW_LEVEL_BUSINESS_RULE_1"));
        assertThat(step2.getTestedRequirements(), hasItem("LOW_LEVEL_BUSINESS_RULE_2"));

    }    

    @Test
    public void the_test_run_can_calculate_all_the_tested_business_rules_in_a_test_run() throws Exception {
        ThucydidesRunner runner = new ThucydidesRunner(SuccessfulSingleTestScenario.class);
        runner.setWebDriverFactory(webDriverFactory);
        runner.run(new RunNotifier());

        List<AcceptanceTestRun> executedScenarios = runner.getAcceptanceTestRuns();        
        AcceptanceTestRun testRun = executedScenarios.get(0);        
        Set<String> allTestedRequirements = testRun.getAllTestedRequirements();
        assertThat(allTestedRequirements, hasItem("SOME_BUSINESS_RULE"));
        assertThat(allTestedRequirements, hasItem("LOW_LEVEL_BUSINESS_RULE"));
        assertThat(allTestedRequirements, hasItem("LOW_LEVEL_BUSINESS_RULE_1"));
        assertThat(allTestedRequirements, hasItem("LOW_LEVEL_BUSINESS_RULE_2"));

    }    
    
}
