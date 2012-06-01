package net.thucydides.jbehave.internals;

import com.thoughtworks.paranamer.Paranamer;
import net.thucydides.core.steps.StepAnnotations;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.jbehave.reflection.Extract;
import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.parsers.RegexPrefixCapturingPatternParser;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.ParameterControls;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.Step;
import org.jbehave.core.steps.StepCandidate;
import org.jbehave.core.steps.StepMonitor;
import org.jbehave.core.steps.StepType;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * A description goes here.
 * User: johnsmart
 * Date: 15/05/12
 * Time: 9:28 PM
 */
public class ThucydidesStepCandidate extends StepCandidate {

    private final StepCandidate stepCandidate;
    private final StepFactory thucydidesStepProxyFactory;

    /*
        public StepCandidate(String patternAsString, int priority, StepType stepType, Method method, Class<?> stepsType,
            InjectableStepsFactory stepsFactory, Keywords keywords, StepPatternParser stepPatternParser,
            ParameterConverters parameterConverters, ParameterControls parameterControls)
     */
    public ThucydidesStepCandidate(StepCandidate stepCandidate, StepFactory thucydidesStepProxyFactory) {

        super(stepCandidate.getPatternAsString(),
                stepCandidate.getPriority(),
                stepCandidate.getStepType(),
                stepCandidate.getMethod(),
                (Class<?>) Extract.field("stepsType").from(stepCandidate),
                (InjectableStepsFactory) Extract.field("stepsFactory").from(stepCandidate),
                (Keywords) Extract.field("keywords").from(stepCandidate),
                new RegexPrefixCapturingPatternParser(),
                new ParameterConverters(),
                new ParameterControls());
        this.stepCandidate = stepCandidate;
        this.thucydidesStepProxyFactory = thucydidesStepProxyFactory;
    }

    @Override
    public Method getMethod() {
        return stepCandidate.getMethod();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Integer getPriority() {
        return stepCandidate.getPriority();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String getPatternAsString() {
        return stepCandidate.getPatternAsString();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Object getStepsInstance() {
        Object stepInstance = super.getStepsInstance();
        StepAnnotations.injectScenarioStepsInto(stepInstance, thucydidesStepProxyFactory);
        return stepInstance;
    }

    @Override
    public StepType getStepType() {
        return stepCandidate.getStepType();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String getStartingWord() {
        return stepCandidate.getStartingWord();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void useStepMonitor(StepMonitor stepMonitor) {
        super.useStepMonitor(stepMonitor);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void doDryRun(boolean dryRun) {
        super.doDryRun(dryRun);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void useParanamer(Paranamer paranamer) {
        super.useParanamer(paranamer);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void composedOf(String[] steps) {
        super.composedOf(steps);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean isComposite() {
        return stepCandidate.isComposite();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String[] composedSteps() {
        return stepCandidate.composedSteps();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean ignore(String stepAsString) {
        return stepCandidate.ignore(stepAsString);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPending() {
        return stepCandidate.isPending();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean matches(String stepAsString) {
        return stepCandidate.matches(stepAsString);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean matches(String step, String previousNonAndStep) {
        return stepCandidate.matches(step, previousNonAndStep);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Step createMatchedStep(String stepAsString, Map<String, String> namedParameters) {
        return stepCandidate.createMatchedStep(stepAsString, namedParameters);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void addComposedSteps(List<Step> steps, String stepAsString, Map<String, String> namedParameters, List<StepCandidate> allCandidates) {
        super.addComposedSteps(steps, stepAsString, namedParameters, allCandidates);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean isAndStep(String stepAsString) {
        return stepCandidate.isAndStep(stepAsString);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean isIgnorableStep(String stepAsString) {
        return stepCandidate.isIgnorableStep(stepAsString);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return stepCandidate.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
