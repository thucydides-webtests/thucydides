package net.thucydides.easyb

import org.easyb.domain.Behavior
import org.easyb.BehaviorStep
import org.easyb.result.Result
import org.easyb.result.ReportingTag
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.easyb.listener.ExecutionListenerAdaptor
import net.thucydides.core.steps.StepListener
import org.easyb.util.BehaviorStepType
import net.thucydides.core.steps.ExecutedStepDescription
import net.thucydides.core.steps.StepFailure
import static org.easyb.util.BehaviorStepType.*;

class ThucydidesExecutionListener extends ExecutionListenerAdaptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesExecutionListener.class);

    private final StepListener stepListener

    ThucydidesExecutionListener(StepListener stepListener) {
        this.stepListener = stepListener
    }

    void startBehavior(Behavior behavior) {
    }

    void startStep(BehaviorStep behaviorStep) {
        if (behaviorStep.stepType in [SCENARIO, GIVEN, WHEN, THEN, AND]) {
            String groupName = groupNameFrom(behaviorStep);
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
        if (result.status() == Result.FAILED) {
            ExecutedStepDescription description = ExecutedStepDescription.withTitle(result.cause.message)
            stepListener.stepFailed(new StepFailure(description, result.cause));
        } else if (result.status() == Result.IGNORED){
            ExecutedStepDescription description = ExecutedStepDescription.withTitle("Ignored step")
            stepListener.stepIgnored(description)
        } else if (result.status() == Result.PENDING) {
            ExecutedStepDescription description = ExecutedStepDescription.withTitle("Pending step")
            stepListener.stepIgnored(description)
        } else {
            stepListener.stepSucceeded();
        }

    }

    void stopBehavior(BehaviorStep behaviorStep, Behavior behavior) {
    }

    void tag(ReportingTag reportingTag) {
    }

    void completeTesting() {
    }
}
