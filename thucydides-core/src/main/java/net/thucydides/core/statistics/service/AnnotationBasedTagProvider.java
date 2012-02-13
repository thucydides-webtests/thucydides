package net.thucydides.core.statistics.service;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.Sets;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.statistics.model.TestRunTag;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;

public class AnnotationBasedTagProvider implements TagProvider {
    public Set<TestRunTag> getTagsFor(final TestOutcome testOutcome) {
        if (testOutcome.getTestCase() == null) {
            return Collections.emptySet();
        }
        List<WithTag> tags = TestAnnotations.forClass(testOutcome.getTestCase()).getTagsForMethod(testOutcome.getMethodName());

        return Sets.newHashSet(convert(tags, toTestRunTags()));
    }

    private Converter<Object, TestRunTag> toTestRunTags() {
        return new Converter<Object, TestRunTag>() {

            @Override
            public TestRunTag convert(Object tag) {
                WithTag withTag = (WithTag) tag;
                return new TestRunTag(withTag.type(), withTag.value(), withTag.value());
            }
        };
    }
}
