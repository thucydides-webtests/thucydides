package net.thucydides.easyb

import net.thucydides.core.model.Story
import net.thucydides.core.model.TestResult
import net.thucydides.core.steps.StepFailure
import net.thucydides.core.steps.StepListener
import org.easyb.BehaviorStep
import org.easyb.domain.Behavior
import org.easyb.listener.ExecutionListenerAdaptor
import org.easyb.result.Result
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import static net.thucydides.core.steps.ExecutedStepDescription.withTitle
import static org.easyb.result.Result.FAILED
import static org.easyb.result.Result.IGNORED
import static org.easyb.result.Result.PENDING
import static org.easyb.util.BehaviorStepType.AND
import static org.easyb.util.BehaviorStepType.GIVEN
import static org.easyb.util.BehaviorStepType.SCENARIO
import static org.easyb.util.BehaviorStepType.STORY
import static org.easyb.util.BehaviorStepType.THEN
import static org.easyb.util.BehaviorStepType.WHEN

class ThucydidesExecutionListener extends ExecutionListenerAdaptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesExecutionListener.class);

    private final StepListener stepListener

    ThucydidesExecutionListener(StepListener stepListener) {
        this.stepListener = stepListener
    }

    void startBehavior(Behavior behavior) {
        noStepsHaveFailed()
    }


    private void noStepsHaveFailed() {
        stepListener.noStepsHaveFailed();
    }

    @Override
    void startStep(BehaviorStep step) {
        LOGGER.debug("THUCYDIDES STARTING STEP $step")
        switch (step.stepType) {
            case STORY:
                Story story = Story.withId("a","b")
                stepListener.testRunStartedFor(story)
            case SCENARIO :
                stepListener.noStepsHaveFailed()
                String groupName = groupNameFrom(step);
                stepListener.stepGroupStarted(groupName)
                break;

            case [GIVEN, WHEN, THEN, AND] :
                String groupName = groupNameFrom(step);
                stepListener.stepGroupStarted(groupName)
                break;
        }
    }

    String groupNameFrom(BehaviorStep step) {

        switch (step.stepType) {
            case GIVEN :
                return "Given $step.name"

            case WHEN :
                return "When $step.name"

            case THEN :
                return "Then $step.name"

            case AND :
                return "And $step.name"

            default :
                return step.name
        }
    }

    @Override
    void stopStep() {
        if (noStepHasFailed()) {
            stepListener.stepGroupFinished(TestResult.SUCCESS)
        } else {
            stepListener.stepGroupFinished()
        }
    }

    def noStepHasFailed() {
        !stepListener.aStepHasFailed();
    }

    void gotResult(Result result) {
        if (stepListener.aStepHasFailed()) {
            skipThisStep()
        } else {
            notifyStepListenerUsingEasybResult(result)
        }

    }



    private def notifyStepListenerUsingEasybResult(Result result) {

        switch (result.status()) {
            case FAILED:
                stepListener.stepFailed(new StepFailure(withTitle(result.cause.message), result.cause));
                break;

            case IGNORED:
                stepListener.updateCurrentStepStatus(TestResult.IGNORED)
                break;

            case PENDING:
                stepListener.updateCurrentStepStatus(TestResult.PENDING)
                break;

            default:
                stepListener.stepSucceeded();

        }
    }

    private void skipThisStep() {
        stepListener.updateCurrentStepStatus(TestResult.SKIPPED)
    }

    void stopBehavior(BehaviorStep behaviorStep, Behavior behavior) {
    }

    void completeTesting() {
    }
}
