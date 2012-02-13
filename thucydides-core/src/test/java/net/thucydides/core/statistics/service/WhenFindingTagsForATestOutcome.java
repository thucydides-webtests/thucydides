package net.thucydides.core.statistics.service;

import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.statistics.model.TestRunTag;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

public class WhenFindingTagsForATestOutcome {

    @Mock
    TestOutcome emptyTestOutcome;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_find_the_annotation_tag_provider_by_default() {
        List<TagProvider> tagProviders = TagProviderService.getTagProviders();
        
        assertThat(tagProviders.size(), is(not(0)));
        assertThat(tagProviders.get(0), instanceOf(AnnotationBasedTagProvider.class));
    }

    @Test
    public void annotation_based_tag_should_return_no_tags_if_the_test_class_is_not_defined() {
        when(emptyTestOutcome.getTestCase()).thenReturn(null);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        assertThat(tagProvider.getTagsFor(emptyTestOutcome).size(), is(0));
    }

    class SomeUnannotatedTestCase {
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tag_should_return_no_annotated_tags_if_no_tags_present_in_the_test_class() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeUnannotatedTestCase.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestRunTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(0));
    }

    @WithTag(value="Car sales", type="pillar")
    class SomeTestCase {
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tag_should_return_annotated_tags_if_tags_present_in_the_test_class() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCase.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestRunTag> tags = tagProvider.getTagsFor(testOutcome);
         assertThat(tags.size(), is(not(0)));
        TestRunTag tag = tags.iterator().next();

        assertThat(tag.getName(), is("Car sales"));
        assertThat(tag.getType(), is("pillar"));
    }

    class SomeTestCaseWithTagOnMethod {
        @WithTag(value="Car sales", type="pillar")
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tag_should_return_annotated_tags_if_tags_present_on_the_test_method() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagOnMethod.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestRunTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(not(0)));
        TestRunTag tag = tags.iterator().next();

        assertThat(tag.getName(), is("Car sales"));
        assertThat(tag.getType(), is("pillar"));
    }

    @WithTag(value="More Car sales", type="pillar")
    class SomeTestCaseWithTagOnMethodAndClass {
        @WithTag(value="Car sales", type="pillar")
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tag_should_return_annotated_tags_from_the_class_and_the_method() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagOnMethodAndClass.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestRunTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(2));
    }


    @WithTags(
            {
                    @WithTag(value="Car sales", type="pillar"),
                    @WithTag(value="Boat sales", type="pillar")
            }
    )
    class SomeTestCaseWithTagsOnClass {
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tags_should_return_multiple_annotated_tags_if_tags_present_on_the_test_class() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagsOnClass.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestRunTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(2));
    }

    class SomeTestCaseWithTagsOnMethod {
        @WithTags(
                {
                        @WithTag(value="Car sales", type="pillar"),
                        @WithTag(value="Boat sales", type="pillar")
                }
        )
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tags_should_return_multiple_annotated_tags_if_tags_present_on_the_test_method() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagsOnMethod.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestRunTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(2));
    }

    @WithTag(value="Online sales", type="pillar")
    class SomeTestCaseWithTagsOnMethodAndClass {
        @WithTags(
                {
                        @WithTag(value="Car sales", type="pillar"),
                        @WithTag(value="Boat sales", type="pillar")
                }
        )
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tags_should_return_multiple_annotated_tags_if_tags_present_on_the_test_method_and_class() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagsOnMethodAndClass.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestRunTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(3));
    }
}

