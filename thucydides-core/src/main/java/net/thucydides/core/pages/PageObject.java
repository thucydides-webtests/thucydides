package net.thucydides.core.pages;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.thucydides.core.annotations.CompatibleWith;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

/**
 * An abstract class representing a WebDriver page object.
 * @author johnsmart
 *
 */
public abstract class PageObject {

    private static final int TIMEOUT = 5;
    
    private static final Map<String, String> MACROS = new HashMap<String, String>();
    {
        MACROS.put("#HOST", "https?://[^/]+");
    }
    private WebDriver driver;
    
    private List<Pattern> matchingPageExpressions = new ArrayList<Pattern>();
    
    public PageObject(WebDriver driver) {
        ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, TIMEOUT);
        this.driver = driver;
        PageFactory.initElements(finder, this);
        fetchMatchingPageExpressions();
    }

    private void fetchMatchingPageExpressions() {
        CompatibleWith compatibleWithAnnotation = this.getClass().getAnnotation(CompatibleWith.class);
        if (compatibleWithAnnotation != null) {
            if (valueIsDefinedFor(compatibleWithAnnotation)) {
                worksWithUrlPattern(compatibleWithAnnotation.value());
            } else {
                worksWithUrlPatternList(compatibleWithAnnotation.urls());
            }
        }
        
    }

    private void worksWithUrlPatternList(final String[] urls) {
        for(String url : urls) {
            worksWithUrlPattern(url);
        }
    }

    private boolean valueIsDefinedFor(final CompatibleWith compatibleWithAnnotation) {
        return ((compatibleWithAnnotation.value() != null) && (compatibleWithAnnotation.value().length() > 0));
    }

    private final void worksWithUrlPattern(final String urlPattern) {
        String processedUrlPattern = substituteMacrosIn(urlPattern);
        
        matchingPageExpressions.add(Pattern.compile(processedUrlPattern));
    }

   
    private String substituteMacrosIn(String urlPattern) {
        String patternWithExpandedMacros = urlPattern;
        for (String macro : MACROS.keySet()) {
            String expanded = MACROS.get(macro);
            patternWithExpandedMacros = patternWithExpandedMacros.replaceAll(macro, expanded);
        }
        return patternWithExpandedMacros;
    }

    public WebDriver getDriver() {
        return driver;
    }
    
    public String getTitle() {
        return driver.getTitle();
    }

    public void setDriver(final WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Does this page object work for this URL?
     * When matching a URL, we check with and without trailing slashes
     * 
     */
    public final boolean compatibleWithUrl(final String currentUrl) {
        if (thereAreNoPatternsDefined()) {
            return true;
        } else {
            return matchUrlAgainstEachPattern(currentUrl);
        }
    }

    private boolean matchUrlAgainstEachPattern(final String currentUrl) {
        boolean pageWorksHere = false;
        for(Pattern pattern : matchingPageExpressions) {
            if (urlIsCompatibleWithThisPattern(currentUrl, pattern)) {
                pageWorksHere = true;
                break;
            }             
        }
        return pageWorksHere;
    }

    private boolean thereAreNoPatternsDefined() {
        return matchingPageExpressions.isEmpty();
    }

    private boolean urlIsCompatibleWithThisPattern(String currentUrl,
            Pattern pattern) {
        return (urlMatchesPatternExactly(pattern, currentUrl) || urlMatchesPatternWithTrailingSlash(pattern, currentUrl));
    }

    private boolean urlMatchesPatternWithTrailingSlash(final Pattern pattern, final String currentUrl) {
        String strippedUrl = currentUrl;
        if (currentUrl.endsWith("/")) {
            strippedUrl = currentUrl.substring(0, currentUrl.length() - 1);
        }
        return urlMatchesPatternExactly(pattern, strippedUrl);
    }

    private boolean urlMatchesPatternExactly(final Pattern pattern, final  String currentUrl) {        
        return pattern.matcher(currentUrl).matches();
    }

}
