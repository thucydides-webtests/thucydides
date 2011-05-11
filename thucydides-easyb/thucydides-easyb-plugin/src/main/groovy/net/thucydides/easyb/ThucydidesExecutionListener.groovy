package net.thucydides.easyb

import org.easyb.domain.Behavior
import org.easyb.BehaviorStep
import org.easyb.result.Result
import static org.easyb.result.Result.*
import org.easyb.result.ReportingTag
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.easyb.listener.ExecutionListenerAdaptor
import net.thucydides.core.steps.StepListener
import org.easyb.util.BehaviorStepType
import net.thucydides.core.steps.ExecutedStepDescription
import net.thucydides.core.steps.StepFailure
import static org.easyb.util.BehaviorStepType.*;
import static net.thucydides.core.steps.ExecutedStepDescription.withTitle

class ThucydidesExecutionListener extends ExecutionListenerAdaptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesExecutionListener.class);

    private final StepListener stepListener

    private boolean stepFailed

    ThucydidesExecutionListener(StepListener stepListener) {
        this.stepListener = stepListener
    }

    void startBehavior(Behavior behavior) {
        stepFailed = false
    }

    void startStep(BehaviorStep step) {
        switch (step.stepType) {
            case SCENARIO :
                stepFailed = false;

            case [GIVEN, WHEN, THEN, AND] :
                String groupName = groupNameFrom(step);
                stepListener.stepGroupStarted(groupName)
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

    void stopStep() {
        stepListener.stepGroupFinished()
    }

    void describeStep(String step) {
    }

    void gotResult(Result result) {

        if (stepFailed) {
            ignoreThisStep()
        } else {
            notifyStepListenerUsingEasybResult(result)
        }

    }

    private def notifyStepListenerUsingEasybResult(Result result) {
        switch (result.status()) {
            case FAILED:
                stepListener.stepFailed(new StepFailure(withTitle(result.cause.message), result.cause));
                stepFailed = true
                break;

            case IGNORED:
                stepListener.stepIgnored(withTitle("Ignored step"))
                break;

            case PENDING:
                stepListener.stepIgnored(withTitle("Pending step"))
                break;

            default:
                stepListener.stepSucceeded();

        }
    }

    private def ignoreThisStep() {
        stepListener.stepIgnored(withTitle("Ignored step"))
    }

    void stopBehavior(BehaviorStep behaviorStep, Behavior behavior) {
    }

    void tag(ReportingTag reportingTag) {
    }

    void completeTesting() {
    }
}
