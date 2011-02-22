package net.thucydides.junit.userstories;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.junit.runners.AbstractWebDriverTest;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.junit.samples.SampleUserStory;
import net.thucydides.junit.samples.ScenarioInStory;

import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class WhenOrganizingTestScenarios extends AbstractWebDriverTest{

    @Test
    public void a_test_case_can_be_associated_with_a_user_story() throws InitializationError {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(ScenarioInStory.class, mockBrowserFactory);

        runner.run(new RunNotifier());
        
        AcceptanceTestRun testRun = runner.getFieldReporter().getAcceptanceTestRun();
        
        assertThat(testRun.getUserStory(), is(not(nullValue())));
        assertThat(testRun.getUserStory().getName(), is(SampleUserStory.class.getName()));
    }
}
