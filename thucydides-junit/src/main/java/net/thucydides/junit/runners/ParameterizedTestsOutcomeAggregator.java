package net.thucydides.junit.runners;

import com.google.common.collect.Lists;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.Runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterizedTestsOutcomeAggregator {
    private final ThucydidesParameterizedRunner thucydidesParameterizedRunner;

    private ParameterizedTestsOutcomeAggregator(ThucydidesParameterizedRunner thucydidesParameterizedRunner) {
        this.thucydidesParameterizedRunner = thucydidesParameterizedRunner;
    }

    public static ParameterizedTestsOutcomeAggregator from(ThucydidesParameterizedRunner thucydidesParameterizedRunner) {
        return new ParameterizedTestsOutcomeAggregator(thucydidesParameterizedRunner);
    }


    public List<TestOutcome> aggregateTestOutcomesByTestMethods() {
        List<TestOutcome> allOutcomes = getTestOutcomesForAllParameterSets();

        if (allOutcomes.isEmpty()) {
            return Lists.<TestOutcome>newArrayList();
        } else {
            return aggregatedScenarioOutcomes(allOutcomes);
        }

    }

    private List<TestOutcome> aggregatedScenarioOutcomes(List<TestOutcome> allOutcomes) {
        Map<String, TestOutcome> scenarioOutcomes = new HashMap<String, TestOutcome>();

        for (TestOutcome testOutcome : allOutcomes) {
            String normalizedMethodName = normalizeMethodName(testOutcome.getMethodName());
            if (scenarioOutcomes.containsKey(normalizedMethodName)) {
                List<TestStep> testSteps = testOutcome.getTestSteps();
                if (!testSteps.isEmpty()) {
                    TestStep nextStep = testSteps.get(0);
                    nextStep.setDescription(normalizeTestStepDescription(nextStep.getDescription(), scenarioOutcomes.get(normalizedMethodName).getTestSteps().size() + 1));
                    scenarioOutcomes.get(normalizedMethodName).recordStep(nextStep);
                }

                scenarioOutcomes.get(normalizedMethodName).getDataTable().addRows(testOutcome.getDataTable().getRows());

            } else {
                TestOutcome scenarioOutcome = createScenarioOutcome(testOutcome);
                scenarioOutcomes.put(scenarioOutcome.getMethodName(), scenarioOutcome);
            }
        }

        List<TestOutcome> aggregatedScenarioOutcomes = new ArrayList<TestOutcome>();
        aggregatedScenarioOutcomes.addAll(scenarioOutcomes.values());
        return aggregatedScenarioOutcomes;

    }

    private String normalizeTestStepDescription(String description, int index) {
        return StringUtils.replace(description, "[1]", "[" + index + "]");
    }

    private TestOutcome createScenarioOutcome(TestOutcome parameterizedOutcome) {
        TestOutcome scenarioOutcome = parameterizedOutcome.withMethodName(normalizeMethodName(parameterizedOutcome.getMethodName()));
        scenarioOutcome.endGroup(); //pop group stack so next item gets added as sibling
        return scenarioOutcome;
    }

    private String normalizeMethodName(String methodName) {
        return methodName.replaceAll("\\[\\d\\]", "");
    }

    public List<TestOutcome> getTestOutcomesForAllParameterSets() {
        List<TestOutcome> testOutcomes = new ArrayList<TestOutcome>();

        testOutcomes.addAll(((ThucydidesRunner) thucydidesParameterizedRunner.getRunners().get(0)).getTestOutcomes());
        for (Runner runner : thucydidesParameterizedRunner.getRunners()) {
            for (TestOutcome testOutcome : ((ThucydidesRunner) runner).getTestOutcomes()) {
                if (!testOutcomes.contains(testOutcome)) {
                    testOutcomes.add(testOutcome);
                }
            }
        }
        return testOutcomes;
    }
}