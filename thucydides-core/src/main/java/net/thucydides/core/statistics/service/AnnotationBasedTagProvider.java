package net.thucydides.core.statistics.service;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.Sets;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;

public class AnnotationBasedTagProvider implements TagProvider {

    public AnnotationBasedTagProvider() {
    }

    public Set<TestTag> getTagsFor(final TestOutcome testOutcome) {
        if (testOutcome.getTestCase() == null) {
            return Collections.emptySet();
        }
        List<TestTag> tags = TestAnnotations.forClass(testOutcome.getTestCase()).getTagsForMethod(testOutcome.getMethodName());

        return Sets.newHashSet(tags);
    }
}
