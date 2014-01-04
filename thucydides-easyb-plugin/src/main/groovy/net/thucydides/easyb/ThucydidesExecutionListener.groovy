package net.thucydides.easyb

import net.thucydides.core.model.TestResult
import net.thucydides.core.steps.ExecutedStepDescription
import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.steps.StepFailure
import org.easyb.BehaviorStep
import org.easyb.domain.Behavior
import org.easyb.listener.ExecutionListenerAdaptor
import org.easyb.result.Result
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static net.thucydides.core.steps.ExecutedStepDescription.withTitle
import static org.easyb.result.Result.*
import static org.easyb.util.BehaviorStepType.*

class ThucydidesExecutionListener extends ExecutionListenerAdaptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesExecutionListener.class);

    def processedSteps = [] as Set

    ThucydidesExecutionListener() {
    }

    void startBehavior(Behavior behavior) {
        LOGGER.debug("THUCYDIDES STARTING BEHAVIOR ${behavior.phrase}")
        StepEventBus.eventBus.clear()
        PluginConfiguration.instance.clearTags()
        PluginConfiguration.instance.clearIssues()
        processedSteps = [] as Set
    }

    private boolean stepFailed

    @Override
    void startStep(BehaviorStep step) {
        LOGGER.debug("THUCYDIDES STARTING STEP $step ($step.id)")
        if (!processedSteps.contains(step.id)) {
            processedSteps.add(step.id)
            switch (step.stepType) {

                case SCENARIO :
                    StepEventBus.eventBus.clear()
                    StepEventBus.eventBus.testStarted(removeAccoladesFrom(groupNameFrom(step)))
                    stepFailed = false;
                    break;

                case [GIVEN, WHEN, THEN, AND] :
                    String groupName = groupNameFrom(step);
                    StepEventBus.eventBus.skippedStepStarted(ExecutedStepDescription.withTitle(removeAccoladesFrom(groupName)))
                    break;
            }
        }
    }

    String removeAccoladesFrom(String name) {
        name.replaceAll("\\{","").replaceAll("\\}","")
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
    }


    void gotResult(Result result) {
        LOGGER.debug("GOT RESULT $result")
        if (StepEventBus.eventBus.areStepsRunning()) {
            notifyStepListenerUsingEasybResult(result)
        } else if (result == TestResult.FAILURE) {
            notifyStepListenerThatTheLastStepFailed(result)
        }
    }

    private def notifyStepListenerThatTheLastStepFailed(Result result) {
        StepEventBus.eventBus.lastStepFailed(new StepFailure(withTitle(result.cause.message), result.cause));
    }

    private def notifyStepListenerUsingEasybResult(Result result) {
        LOGGER.debug("NOTIFYING STEP RESULT $result")
        switch (result.status()) {
            case FAILED:
                StepEventBus.eventBus.stepFailed(new StepFailure(withTitle(result.cause.message), result.cause));
                break;

            case IGNORED:
                StepEventBus.eventBus.stepIgnored()
                break;

            case PENDING:
                StepEventBus.eventBus.stepPending()
                break;

            case SUCCEEDED:
                StepEventBus.eventBus.stepFinished();

            default:
                if (aPreviousStepHasFailed()) {
                    StepEventBus.eventBus.stepIgnored();
                }
                break;
        }
    }


    private boolean aPreviousStepHasFailed() {
        if (stepFailed) {
            return stepFailed
        } else {
            if (StepEventBus.eventBus.aStepInTheCurrentTestHasFailed()) {
                stepFailed = true;
                return false;
            }
        }
    }

    void completeTesting() {
        // TODO: Make sure easyb calls this method
        StepEventBus.eventBus.testSuiteFinished();

    }
}
