package net.thucydides.junit.pipeline

import net.thucydides.core.annotations.Steps
import net.thucydides.junit.runners.ThucydidesRunner
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification
import spock.lang.IgnoreRest

class WhenProcessingATestDataPipeline extends Specification {

    public static executedStages = []
    public static class SimplePipeline extends TestPipeline {
        @DataProvider
        def data() { [["field":1]] }
        int field

        @Stage(0)
        public void stage0() {executedStages = []}
        @Stage(1)
        public void stage1() { executedStages << "stage1" }

        @Stage(2)
        public void stage2() { executedStages << "stage2" }

        @Stage(3)
        public void stage3() { executedStages << "stage3" }
    }

    def "each stage should be executed sequentially"() {
        given:
            def runner = new ThucydidesTestPipelineRunner(SimplePipeline);
        when:
            runner.run(new RunNotifier());
        then:
            executedStages == ["stage1", "stage2", "stage3"]
    }


    public static int counterTotal
    public static class SimplePipelineWithData extends TestPipeline {

        @DataProvider
        def data() {
            [["counter":10]]
        }

        int counter

        @Stage(1)
        public void stage1() { counter++ }

        @Stage(2)
        public void stage2() { counter++ }

        @Stage(3)
        public void stage3() { counter++; counterTotal = counter; }
    }

    def "provided data maps should initialize fields in the pipeline"() {
        given:
            def runner = new ThucydidesTestPipelineRunner(SimplePipelineWithData);
        when:
            runner.run(new RunNotifier());
        then:
            counterTotal == 13
    }


    public static int totalAgeInTenYears = 0
    public static class PipelineUsingDataFromACSVFile extends TestPipeline {

        @DataProvider
        def data() {
            loadFromCSVFile("data.csv")
        }

        String name;
        int age;

        @Stage(1)
        public void stage1() { totalAgeInTenYears = 0; age = age + 10 }

        @Stage(2)
        public void stage2() { totalAgeInTenYears += age; }
    }

    def "Should load test data from a CSV file"() {
        given:
            def runner = new ThucydidesTestPipelineRunner(PipelineUsingDataFromACSVFile);
        when:
            runner.run(new RunNotifier());
        then:
            totalAgeInTenYears == 250
    }

    public static int multitestCounter = 0
    public static class SimplePipelineWithSeveralDataSets extends TestPipeline {

        @DataProvider
        List<Map> data() {
            [["counter":10], ["counter":20], ["counter":30]]
        }

        int counter

        @Stage(1)
        public void stage1() { multitestCounter = 0; counter++ }

        @Stage(2)
        public void stage2() { counter++ }

        @Stage(3)
        public void stage3() { multitestCounter += counter; }
    }

    def "when several data sets are provided tests should be run once for each data set"() {
        given:
            def runner = new ThucydidesTestPipelineRunner(SimplePipelineWithSeveralDataSets);
        when:
            runner.run(new RunNotifier());
        then:
            multitestCounter == 66
    }

    public static int thucydidesMultitestCounter = 0
    @RunWith(ThucydidesRunner)
    public static class ThucydidesEnabledPipelineWithSeveralDataSets extends TestPipeline {

        @DataProvider
        List<Map> data() {
            [["counter":10], ["counter":20], ["counter":30]]
        }

        @Steps
        SomeSteps someSteps;

        int counter

        @Stage(1)
        public void stage1() { thucydidesMultitestCounter = 0; println "step1 counter = $counter"; someSteps.step1(); counter++ }

        @Stage(2)
        public void stage2() { println "step2 counter = $counter"; someSteps.step2(); counter++ }

        @Stage(3)
        public void stage3() { println "step3 counter = $counter"; someSteps.step3(); thucydidesMultitestCounter += counter; }
    }


    def "Thucydides runner should initialize steps correctly"() {
        given:
            def runner = new ThucydidesRunner(ThucydidesEnabledPipelineWithSeveralDataSets);
        when:
            runner.run(new RunNotifier());
        then:
            thucydidesMultitestCounter == 66
    }

    def "Thucydides runner should report one test outcome per dataset"() {
        given:
            def runner = new ThucydidesPipelineRunner(ThucydidesEnabledPipelineWithSeveralDataSets);
        when:
            runner.run(new RunNotifier());
        then:
            runner.testOutcomes.size() == 3
            runner.testOutcomes[0].testSteps.collect {it.description} == ["Step1", "Step2", "Step3"]
            runner.testOutcomes[1].testSteps.collect {it.description} == ["Step1", "Step2", "Step3"]
            runner.testOutcomes[2].testSteps.collect {it.description} == ["Step1", "Step2", "Step3"]
    }


    // TODO: Should report thucydides test results for each
    // TODO: Should write results to an Excel spreadsheet

    // TODO: check type of dataprovider field
    // TODO: should read from working directory using a variable
    // TODO: should read from home directory using a variable
    // TODO: should read from a file in a directory provided in a system parameter
    // TODO: handle date fields
    // TODO: handle enums
    // TODO: handle doubles
    // TODO handle incorrect field names
    // TODO: should produce JUnit results
    // TODO: should run thucydides tests

}
