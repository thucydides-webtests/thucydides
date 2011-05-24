package net.thucydides.core.junit.rules;


import net.thucydides.core.ThucydidesSystemProperty;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class WhenSavingAndRestoringSystemProperties {

    private static final String SYSTEM_PROPERTY = ThucydidesSystemProperty.REPORT_RESOURCE_PATH.getPropertyName();
    @Mock
    Statement statement;

    @Mock
    FrameworkMethod frameworkMethod;

    @Mock
    Object testClass;

    @Rule
    public SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule();

    String originalPropertyValue;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void webdriver_system_properties_should_be_saved_and_restored_when_a_test_modifies_them() throws Throwable {

        System.setProperty(SYSTEM_PROPERTY,"original-value");

        SaveWebdriverSystemPropertiesRule rule = new SaveWebdriverSystemPropertiesRule();
        Statement statementWithRule = rule.apply(statement, frameworkMethod, testClass);

        System.setProperty(SYSTEM_PROPERTY,"new-value-for-tests");

        statementWithRule.evaluate();

        assertThat(System.getProperty(SYSTEM_PROPERTY), is("original-value"));

    }

    @Test
    public void originally_empty_webdriver_system_properties_should_be_removed() throws Throwable {

        System.clearProperty(SYSTEM_PROPERTY);

        SaveWebdriverSystemPropertiesRule rule = new SaveWebdriverSystemPropertiesRule();
        Statement statementWithRule = rule.apply(statement, frameworkMethod, testClass);

        System.setProperty(SYSTEM_PROPERTY, "http://www.amazon.com");

        statementWithRule.evaluate();

        assertThat(System.getProperty(SYSTEM_PROPERTY), is(nullValue()));
    }


}
