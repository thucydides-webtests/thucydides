package net.thucydides.core.pages;

import net.thucydides.core.annotations.CompatibleWith;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

public class WhenMatchingUrlsToPages {

    @Mock
    WebDriver webdriver;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    final class PageObjectWithNoUrlDefinitions extends PageObject {
        public PageObjectWithNoUrlDefinitions(WebDriver driver) {
            super(driver);
        }
    }
    
    @Test
    public void by_default_all_urls_will_work_with_a_page() {
        PageObjectWithNoUrlDefinitions page = new PageObjectWithNoUrlDefinitions(webdriver);
        assertThat(page.compatibleWithUrl("http://www.apache.org"), is(true));
    }

    @CompatibleWith("http://www.apache.org")
    final class PageObjectWithHardcodedUrlDefinition extends PageObject {
        public PageObjectWithHardcodedUrlDefinition(WebDriver driver) {
            super(driver);
        }
    }
    
    @Test
    public void defining_a_url_will_limit_compatiblity_to_that_url() {
        PageObject page = new PageObjectWithHardcodedUrlDefinition(webdriver);
        assertThat(page.compatibleWithUrl("http://www.apache.org"), is(true));
    }

    @Test
    public void should_ignore_trailing_slashes_in_incoming_url() {
        PageObject page = new PageObjectWithHardcodedUrlDefinition(webdriver);
        assertThat(page.compatibleWithUrl("http://www.apache.org/"), is(true));
    }

    @Test
    public void defining_a_url_will_deny_compatiblity_with_a_different_url() {
        PageObject page = new PageObjectWithHardcodedUrlDefinition(webdriver);
        assertThat(page.compatibleWithUrl("http://projects.apache.org"), is(false));
    }
    
    @CompatibleWith("http://.*.apache.org")
    final class PageObjectForAnyApacheProject extends PageObject {
        public PageObjectForAnyApacheProject(WebDriver driver) {
            super(driver);
        }
    }
    
    @Test
    public void should_allow_urls_with_wildcards() {
        PageObject page = new PageObjectForAnyApacheProject(webdriver);
        assertThat(page.compatibleWithUrl("http://lucene.apache.org"), is(true));
    }

    @CompatibleWith("http://maven.apache.org/.*")
    final class PageObjectForAnyMavenProjectPage extends PageObject {
        public PageObjectForAnyMavenProjectPage(WebDriver driver) {
            super(driver);
        }
    }
    
    @Test
    public void should_allow_urls_with_trailing_wildcards() {
        PageObject page = new PageObjectForAnyMavenProjectPage(webdriver);
        assertThat(page.compatibleWithUrl("http://maven.apache.org/what-is-maven.html"), is(true));
    }

    @CompatibleWith("http://.*/whatever/comes/next")
    final class PageObjectWithWildcardServerName extends PageObject {
        public PageObjectWithWildcardServerName(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_allow_wildcard_server_names() {
        PageObject page = new PageObjectWithWildcardServerName(webdriver);
        assertThat(page.compatibleWithUrl("http://maven.apache.org/whatever/comes/next"), is(true));
    }

    @CompatibleWith(urls={"http://maven.apache.org","lucene.apache.org"})
    final class MavenOrLucenePage extends PageObject {
        public MavenOrLucenePage(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_allow_multiple_urls() {
        PageObject page = new PageObjectForAnyApacheProject(webdriver);
        assertThat(page.compatibleWithUrl("http://lucene.apache.org"), is(true));
        assertThat(page.compatibleWithUrl("http://maven.apache.org"), is(true));
    }

    @CompatibleWith(urls={"#HOST/"})
    final class GenericHomePage extends PageObject {
        public GenericHomePage(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_allow_a_generic_home_page_definition() {
        PageObject page = new GenericHomePage(webdriver);
        assertThat(page.compatibleWithUrl("http://lucene.apache.org/"), is(true));
        assertThat(page.compatibleWithUrl("http://maven.apache.org/something"), is(false));
        assertThat(page.compatibleWithUrl("http://maven.apache.org/something/"), is(false));
    }

    @CompatibleWith(urls={"#HOST/somewhere"})
    final class GenericInternalPage extends PageObject {
        public GenericInternalPage(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_allow_a_server_independent_page_definition() {
        PageObject page = new GenericInternalPage(webdriver);
        assertThat(page.compatibleWithUrl("http://lucene.apache.org/somewhere"), is(true));
        assertThat(page.compatibleWithUrl("http://maven.apache.org/somewhere"), is(true));
        assertThat(page.compatibleWithUrl("http://maven.apache.org/somethingelse"), is(false));
    }
    
}
