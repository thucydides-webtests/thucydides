package net.thucydides.core.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.*;

import static net.thucydides.core.model.TestResult.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Parameterized.class)
public class WhenEvaluatingOverallResults {

    private List<TestResult> results;
    private TestResult expectedOverallResult;

    public WhenEvaluatingOverallResults(List<TestResult> results, TestResult expectedOverallResult) {
        this.results = results;
        this.expectedOverallResult = expectedOverallResult;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][] {
                { Collections.emptyList(),                  PENDING },
                { Arrays.asList(SUCCESS),                   SUCCESS },
                { Arrays.asList(SUCCESS, SUCCESS),          SUCCESS },
                { Arrays.asList(SUCCESS, SUCCESS, SUCCESS), SUCCESS },
                { Arrays.asList(SUCCESS, PENDING),          PENDING },
                { Arrays.asList(SUCCESS, IGNORED),          SUCCESS },
                { Arrays.asList(FAILURE),                   FAILURE },
                { Arrays.asList(FAILURE, FAILURE),          FAILURE },
                { Arrays.asList(FAILURE, SUCCESS),          FAILURE },
                { Arrays.asList(FAILURE, IGNORED),          FAILURE },
                { Arrays.asList(FAILURE, PENDING),          FAILURE },
                { Arrays.asList(IGNORED),                   IGNORED },
                { Arrays.asList(IGNORED, IGNORED),          IGNORED },
                { Arrays.asList(IGNORED, PENDING),          PENDING },
                { Arrays.asList(PENDING),                   PENDING },
                { Arrays.asList(PENDING, PENDING),          PENDING },
        });
    }

    @Test
    public void should_produce_correct_overall_result_from_a_list_of_step_results() {

        TestResultList overallResult = new TestResultList(results);

        assertThat(overallResult.getOverallResult(), is(expectedOverallResult));
    }
    /*
    @Test
    public void when_no_results_are_present_the_overall_result_is_a_success() {

        List<TestResult> results = Collections.emptyList();

        TestResultList overallResult = new TestResultList(results);

        assertThat(overallResult.getOverallResult(), is(SUCCESS));
    }

    @Test
    public void when_all_results_are_successful_the_overall_result_is_a_success() {

        List<TestResult> results = Arrays.asList(SUCCESS, SUCCESS, SUCCESS);

        TestResultList overallResult = new TestResultList(results);

        assertThat(overallResult.getOverallResult(), is(SUCCESS));
    }

    @Test
    public void if_a_failure_is_present_the_overall_result_is_a_failure() {

        List<TestResult> results = Arrays.asList(SUCCESS, SUCCESS, FAILURE);

        TestResultList overallResult = new TestResultList(results);

        assertThat(overallResult.getOverallResult(), is(FAILURE));
    }

    @Test
    public void if_a_pending_step_is_present_the_overall_result_is_pending() {

        List<TestResult> results = Arrays.asList(SUCCESS, SUCCESS, PENDING);

        TestResultList overallResult = new TestResultList(results);

        assertThat(overallResult.getOverallResult(), is(PENDING));
    }

    @Test
    public void if_an_ignored_step_is_present_among_other_steps_the_overall_result_is_determined_by_the_other_steps() {

        List<TestResult> results = Arrays.asList(SUCCESS, SUCCESS, IGNORED);

        TestResultList overallResult = new TestResultList(results);

        assertThat(overallResult.getOverallResult(), is(SUCCESS));
    }

    @Test
    public void if_an_ignored_step_is_present_among_other_steps_the_overall_result_is_determined_by_the_other_steps() {

        List<TestResult> results = Arrays.asList(PENDING, IGNORED, PENDING);

        TestResultList overallResult = new TestResultList(results);

        assertThat(overallResult.getOverallResult(), is(PENDING));
    }
        */
}
