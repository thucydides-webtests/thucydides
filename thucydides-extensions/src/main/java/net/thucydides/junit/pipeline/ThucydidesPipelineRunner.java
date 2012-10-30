package net.thucydides.junit.pipeline;

import com.google.common.collect.Lists;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

class ThucydidesPipelineRunner extends ThucydidesRunner {
    ThucydidesPipelineRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    public List<TestOutcome> getTestOutcomes() {
        List<TestOutcome> flatTestOutcomes = super.getTestOutcomes();
        List<TestOutcome> expandedTestOutcomes = Lists.newArrayList();

        for(TestOutcome testOutcome : flatTestOutcomes) {
            expandedTestOutcomes.addAll(extractPipelineTestsFrom(testOutcome));
        }
        return expandedTestOutcomes;
    }

    private List<? extends TestOutcome> extractPipelineTestsFrom(TestOutcome baseTestOutcome) {

        List<TestStep> steps = baseTestOutcome.getTestSteps();

        int numberOfOutcomes = countFirstRound(steps);
        List<TestOutcome> testOutcomePerPipeline = createTargetTestOutcomes(baseTestOutcome, numberOfOutcomes);

        int stepCounter = 0;
        for(TestStep step : steps) {
            TestOutcome currentTestOutcome = testOutcomePerPipeline.get(stepCounter % numberOfOutcomes);
            currentTestOutcome.recordStep(step);
            stepCounter++;
        }
        return testOutcomePerPipeline;
    }

    private List<TestOutcome> createTargetTestOutcomes(TestOutcome baseTestOutcome, int testOutcomeCount) {
        List<TestOutcome> targetTestOutcomes = Lists.newArrayList();
        for(int outcomeIndex = 1; outcomeIndex <= testOutcomeCount; outcomeIndex++) {
            String qualifiedTitle = baseTestOutcome.getTitle() + " [" + outcomeIndex + "]";
            TestOutcome newTestOutcome = TestOutcome.emptyCopyOf(baseTestOutcome);
            newTestOutcome.setTitle(qualifiedTitle);
            targetTestOutcomes.add(newTestOutcome);
        }
        return targetTestOutcomes;
    }

    private int countFirstRound(List<TestStep> steps) {
        int firstRoundStepCount = 0;
        String firstStepName = steps.get(0).getDescription();
        for(TestStep step : steps) {
            if (step.getDescription().equals(firstStepName)) {
                firstRoundStepCount++;
            } else {
                break;
            }
        }
        return firstRoundStepCount;
    }

}
