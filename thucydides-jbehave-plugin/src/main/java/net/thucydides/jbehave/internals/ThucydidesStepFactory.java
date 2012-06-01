package net.thucydides.jbehave.internals;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.Lists;
import net.sf.extcos.ComponentQuery;
import net.sf.extcos.ComponentScanner;
import net.thucydides.core.steps.StepAnnotations;
import net.thucydides.core.steps.StepFactory;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.steps.AbstractStepsFactory;
import org.jbehave.core.steps.CandidateSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;

public class ThucydidesStepFactory extends AbstractStepsFactory {

    /**
     * Provides a proxy of the ScenarioSteps object used to invoke the test steps.
     * This proxy notifies the test runner about individual step outcomes.
     */
    private StepFactory thucydidesStepProxyFactory;

    private final ThucydidesStepContext context;

    private final String rootPackage;

    public ThucydidesStepFactory(Configuration configuration, String rootPackage) {
        super(configuration);
        this.thucydidesStepProxyFactory = newStepProxyFactory();
        this.context = new ThucydidesStepContext();
        this.rootPackage = rootPackage;
    }

    private StepFactory newStepProxyFactory() {
        return new StepFactory();
    }

    public List<CandidateSteps> createCandidateSteps() {
        List<CandidateSteps> coreCandidateSteps = super.createCandidateSteps();
        return convert(coreCandidateSteps, toThucydidesCandidateSteps());
    }

    @Override
    protected List<Class<?>> stepsTypes() {
        List<Class<?>> types = new ArrayList<Class<?>>();
        for (Class candidateClass : getCandidateClasses() ){
            if (hasAnnotatedMethods(candidateClass)) {
                types.add(candidateClass);
            }
        }
        return types;
    }

    private List<Class> getCandidateClasses() {
        ComponentScanner scanner = new ComponentScanner();

        Set<Class<?>> allClassesUnderRootPackage = scanner.getClasses(new ComponentQuery() {
            protected void query() {
                select().from(rootPackage).returning(all());
            }
        });

        List<Class> candidateClasses = Lists.newArrayList();
        for(Class<?> classUnderRootPackage : allClassesUnderRootPackage) {
            if (hasAnnotatedMethods(classUnderRootPackage)) {
                candidateClasses.add(classUnderRootPackage);
            }
        }

        return candidateClasses;
    }

    private Converter<CandidateSteps, CandidateSteps> toThucydidesCandidateSteps() {
        return new Converter<CandidateSteps, CandidateSteps>() {
            public CandidateSteps convert(CandidateSteps candidateSteps) {
                return new ThucydidesCandidateSteps(candidateSteps, thucydidesStepProxyFactory);
            }
        };
    }

    public Object createInstanceOfType(Class<?> type) {
        Object stepsInstance = context.newInstanceOf(type);
        StepAnnotations.injectScenarioStepsInto(stepsInstance, thucydidesStepProxyFactory);
        return stepsInstance;
    }

    public static ThucydidesStepFactory withStoriesFromPackage(String rootPackage) {
        return new ThucydidesStepFactory(ThucydidesJBehave.defaultConfiguration(), rootPackage);
    }

    public ThucydidesStepFactory andConfiguration(Configuration configuration) {
        return new ThucydidesStepFactory(configuration, this.rootPackage);
    }

}
