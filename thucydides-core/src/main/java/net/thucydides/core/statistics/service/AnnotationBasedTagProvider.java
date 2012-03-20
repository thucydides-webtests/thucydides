package net.thucydides.core.statistics.service;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.Sets;
import net.thucydides.core.Thucydides;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.statistics.model.TestRunTag;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.project;

public class AnnotationBasedTagProvider implements TagProvider {
    
    private final String projectKey;

    public AnnotationBasedTagProvider() {
        projectKey = getProjectKey();
    }

    private String getProjectKey() {
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        return ThucydidesSystemProperty.PROJECT_KEY.from(environmentVariables, Thucydides.DEFAULT_PROJECT_KEY);
    }

    public Set<TestRunTag> getTagsFor(final TestOutcome testOutcome) {
        if (testOutcome.getTestCase() == null) {
            return Collections.emptySet();
        }
        List<WithTag> tags = TestAnnotations.forClass(testOutcome.getTestCase()).getTagsForMethod(testOutcome.getMethodName());

        return Sets.newHashSet(convert(tags, toTestRunTags(projectKey)));
    }

    private Converter<Object, TestRunTag> toTestRunTags(final String projectKey) {
        return new Converter<Object, TestRunTag>() {

            @Override
            public TestRunTag convert(Object tag) {
                WithTag withTag = (WithTag) tag;
                return new TestRunTag(projectKey, withTag.type(), withTag.value(), withTag.value());
            }
        };
    }
}
