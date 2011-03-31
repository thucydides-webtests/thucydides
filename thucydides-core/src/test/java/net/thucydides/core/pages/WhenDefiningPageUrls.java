package net.thucydides.core.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static net.thucydides.core.pages.PageObject.withParameters;

public class WhenDefiningPageUrls {

    @Mock
    WebDriver webdriver;
    
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

}
