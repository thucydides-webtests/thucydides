package net.thucydides.core.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static net.thucydides.core.pages.PageObject.withParameters;

public class WhenDefiningPageUrls {

    @Mock
    WebDriver webdriver;

    @Rule
    public SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @DefaultUrl("http://www.apache.org")
    final class PageObjectWithFullUrlDefinition extends PageObject {
        public PageObjectWithFullUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }
    
    @DefaultUrl("http://test.myapp.org/somepage")
    final class PageObjectWithFullUrlAndPageDefinition extends PageObject {
        public PageObjectWithFullUrlAndPageDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @DefaultUrl("http://test.myapp.org:9000/somepage")
    final class PageObjectWithFullUrlAndPageAndPortDefinition extends PageObject {
        public PageObjectWithFullUrlAndPageAndPortDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_annotation_should_determine_where_the_page_will_open_to() {
        PageObject page = new PageObjectWithFullUrlDefinition(webdriver);

        page.open();

        verify(webdriver).get("http://www.apache.org");
    }

    final class PageObjectWithNoUrlDefinition extends PageObject {
        public PageObjectWithNoUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_webdriver_base_url_system_property_should_not_override_pages() {
        PageObject page = new PageObjectWithFullUrlAndPageDefinition(webdriver);
        System.setProperty("webdriver.base.url","http://staging.myapp.org");
        page.open();

        verify(webdriver).get("http://staging.myapp.org/somepage");
    }

    @Test
    public void the_webdriver_base_url_system_property_should_override_protocol() {
        PageObject page = new PageObjectWithFullUrlAndPageDefinition(webdriver);
        System.setProperty("webdriver.base.url","https://staging.myapp.org");
        page.open();

        verify(webdriver).get("https://staging.myapp.org/somepage");
    }

    @Test
    public void the_webdriver_base_url_system_property_should_override_ports() {
        PageObject page = new PageObjectWithFullUrlAndPageAndPortDefinition(webdriver);
        System.setProperty("webdriver.base.url","https://staging.myapp.org:8888");
        page.open();

        verify(webdriver).get("https://staging.myapp.org:8888/somepage");
    }

    @Test
    public void the_base_url_is_overrided_by_the_webdriver_base_url_system_property() {
        PageObject page = new PageObjectWithFullUrlDefinition(webdriver);
        System.setProperty("webdriver.base.url","http://www.wikipedia.org");
        page.open();

        verify(webdriver).get("http://www.wikipedia.org");
    }

    @Test
    public void the_base_url_should_be_used_if_no_url_annotation_is_present() {
        PageObject page = new PageObjectWithNoUrlDefinition(webdriver);
        PageConfiguration.getCurrentConfiguration().setDefaultBaseUrl("http://www.google.com");

        page.open();

        verify(webdriver).get("http://www.google.com");
    }

    @DefaultUrl("http://jira.mycompany.org/issues/{1}")
    final class PageObjectWithParameterizedUrlDefinition extends PageObject {
        public PageObjectWithParameterizedUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_annotation_should_let_you_define_a_parameterized_url() {
        PageObject page = new PageObjectWithParameterizedUrlDefinition(webdriver);

        page.open(withParameters("ISSUE-1"));

        verify(webdriver).get("http://jira.mycompany.org/issues/ISSUE-1");
    }

    @Test
    public void the_pages_object_provides_access_to_the_webdriver_instance() {
        PageObject page = new PageObjectWithParameterizedUrlDefinition(webdriver);

        page.getDriver().get("http://www.google.com");

        verify(webdriver).get("http://www.google.com");
    }
    
    @DefaultUrl("http://jira.mycompany.org")
    @NamedUrls(
      {
        @NamedUrl(name = "open.issue", url = "http://jira.mycompany.org/issues/{1}")
      }
    )
    final class PageObjectWithNamedParameterizedUrlDefinition extends PageObject {
        public PageObjectWithNamedParameterizedUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_annotation_should_let_you_define_a_named_parameterized_url() {
        PageObject page = new PageObjectWithNamedParameterizedUrlDefinition(webdriver);

        page.open("open.issue", withParameters("ISSUE-1"));

        verify(webdriver).get("http://jira.mycompany.org/issues/ISSUE-1");
    }

    @DefaultUrl("/clients")
    final class PageObjectWithRelaticeUrlDefinition extends PageObject {
        public PageObjectWithRelaticeUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_annotation_can_be_relative_to_the_base_url() {
        PageObject page = new PageObjectWithRelaticeUrlDefinition(webdriver);
        PageConfiguration.getCurrentConfiguration().setDefaultBaseUrl("http://myapp.mycompany.com");

        page.open();

        verify(webdriver).get("http://myapp.mycompany.com/clients");
    }


    @DefaultUrl("http://jira.mycompany.org")
    @NamedUrls(
      {
        @NamedUrl(name = "open.issue", url = "/issues/{1}")
      }
    )
    final class PageObjectWithDefaultUrlAndNamedParameterizedRelativeUrlDefinition extends PageObject {
        public PageObjectWithDefaultUrlAndNamedParameterizedRelativeUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }


    @Test
    public void the_webdriver_base_url_system_property_should_not_override_pages_with_parameters() {
        PageObject page = new PageObjectWithDefaultUrlAndNamedParameterizedRelativeUrlDefinition(webdriver);
        System.setProperty("webdriver.base.url","http://staging.mycompany.org");
        page.open("open.issue", withParameters("ISSUE-1"));

        verify(webdriver).get("http://staging.mycompany.org/issues/ISSUE-1");
    }


    @Test
    public void the_url_annotation_should_let_you_define_a_named_parameterized_url_relative_to_the_default_url() {
        PageObject page = new PageObjectWithDefaultUrlAndNamedParameterizedRelativeUrlDefinition(webdriver);
        PageConfiguration.getCurrentConfiguration().setDefaultBaseUrl(null);
        page.open("open.issue", withParameters("ISSUE-1"));

        verify(webdriver).get("http://jira.mycompany.org/issues/ISSUE-1");
    }

    @NamedUrls(
      {
        @NamedUrl(name = "open.issue", url = "/issues/{1}")
      }
    )
    final class PageObjectWithNamedParameterizedRelativeUrlDefinition extends PageObject {
        public PageObjectWithNamedParameterizedRelativeUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_url_annotation_should_let_you_define_a_relative_named_parameterized_url() {
        PageObject page = new PageObjectWithNamedParameterizedRelativeUrlDefinition(webdriver);
        PageConfiguration.getCurrentConfiguration().setDefaultBaseUrl("http://myapp.mycompany.com");

        page.open("open.issue", withParameters("ISSUE-1"));

        verify(webdriver).get("http://myapp.mycompany.com/issues/ISSUE-1");
    }

    @NamedUrls(
      {
              @NamedUrl(name = "open.issue", url = "/issues/{1}"),
              @NamedUrl(name = "close.issue", url = "/issues/close/{1}")
      }
    )
    final class PageObjectWithMultipleNamedUrlDefinitions extends PageObject {
        public PageObjectWithMultipleNamedUrlDefinitions(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void when_the_default_url_is_defined_as_a_classpath_url_it_uses_an_absolute_path_from_the_classpath() {
        PageWithDefaultUrlOnTheClasspath page = new PageWithDefaultUrlOnTheClasspath(webdriver);

        URL staticSiteUrl = Thread.currentThread().getContextClassLoader().getResource("static-site/index.html");

        page.open();

        verify(webdriver).get(staticSiteUrl.toString());

    }

    @Test(expected = IllegalStateException.class)
    public void if_a_classpath_url_is_not_found_an_exception_is_thrown() {
        PageWithInvalidDefaultUrlOnTheClasspath page = new PageWithInvalidDefaultUrlOnTheClasspath(webdriver);

        page.open();

    }
    @Test
    public void the_url_annotation_should_let_you_define_several_named_parameterized_urls() {
        PageObject page = new PageObjectWithMultipleNamedUrlDefinitions(webdriver);
        PageConfiguration.getCurrentConfiguration().setDefaultBaseUrl("http://myapp.mycompany.com");

        page.open("close.issue", withParameters("ISSUE-1"));

        verify(webdriver).get("http://myapp.mycompany.com/issues/close/ISSUE-1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void the_url_annotation_should_throw_an_exception_if_no_named_url_is_found() {
        PageObject page = new PageObjectWithMultipleNamedUrlDefinitions(webdriver);
        PageConfiguration.getCurrentConfiguration().setDefaultBaseUrl("http://myapp.mycompany.com");

        page.open("no.such.template", withParameters("ISSUE-1"));
    }

    @Test
    public void when_we_get_a_target_url_a_normal_url_is_left_unprocessed() {
        String url = PageUrls.getUrlFrom("http://www.google.com");

        assertThat(url, is("http://www.google.com"));
    }

    @Test
    public void when_we_get_a_target_url_a_normal_https_url_is_left_unprocessed() {
        String url = PageUrls.getUrlFrom("https://www.google.com");

        assertThat(url, is("https://www.google.com"));
    }

    @Test
    public void when_we_get_a_target_url_a_classpath_url_is_converted_to_a_file_url() {
        String staticSiteUrl = Thread.currentThread().getContextClassLoader()
                                                     .getResource("static-site/index.html").toString();
        String url = PageUrls.getUrlFrom("classpath:static-site/index.html");

        assertThat(url, is(staticSiteUrl));
    }
}
