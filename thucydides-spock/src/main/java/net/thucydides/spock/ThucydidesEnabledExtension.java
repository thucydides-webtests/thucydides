package net.thucydides.spock;

import net.thucydides.core.bootstrap.ThucydidesAgent;
import org.apache.commons.lang.StringUtils;
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.FieldInfo;
import org.spockframework.runtime.model.MethodInfo;
import org.spockframework.runtime.model.SpecInfo;

public class ThucydidesEnabledExtension extends AbstractAnnotationDrivenExtension<ThucydidesEnabled> {

    private ThucydidesAgent agent;

    public ThucydidesEnabledExtension() {
    }

    public void visitSpecAnnotation(ThucydidesEnabled annotation, SpecInfo spec) {
        agent = new ThucydidesAgent();
        spec.addListener(new ThucydidesRunListener(agent));
        spec.getInitializerMethod().addInterceptor(new ThucydidesInterceptor(agent));
    }

    @Override
    public void visitFeatureAnnotation(ThucydidesEnabled annotation, FeatureInfo feature) {
        System.out.println("visitFeatureAnnotation");
    }

    @Override
    public void visitFixtureAnnotation(ThucydidesEnabled annotation, MethodInfo fixtureMethod) {
        System.out.println("visitFixtureAnnotation");
    }

    @Override
    public void visitFieldAnnotation(ThucydidesEnabled annotation, FieldInfo field) {
        System.out.println("visitFieldAnnotation");
    }

    @Override
    public void visitSpec(SpecInfo spec) {
        System.out.println("visitSpec");
    }
}
