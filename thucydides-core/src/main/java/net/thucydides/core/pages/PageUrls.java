package net.thucydides.core.pages;

import com.sun.jndi.toolkit.url.UrlUtil;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;

import java.net.URL;

/**
 * Manage the URLs associated with a page
 * Urls can be associated with a page using annotations or via the default configuration properties.
 * The DefaultUrl annotation defines the default starting point for a page. If none is defined, the
 * system default URL is used.
 * The NamedUrl and NamedUrls annotations can be used to define query URLs, optionally with parameters.
 */
class PageUrls {
    private static final String CLASSPATH_URL_PREFIX = "classpath:";
    private static final int CLASSPATH_URL_PREFIX_LENGTH = CLASSPATH_URL_PREFIX.length();
    private Object pageObject;

    private String pageLevelDefaultBaseUrl;

    public PageUrls(final Object pageObject) {
        this.pageObject = pageObject;
    }

    public String getStartingUrl() {
        DefaultUrl urlAnnotation = pageObject.getClass().getAnnotation(DefaultUrl.class);
        if (urlAnnotation != null) {
            String annotatedBaseUrl = urlAnnotation.value();
            String startingUrl = getUrlFrom(annotatedBaseUrl);
            return addDefaultBaseUrlIfRelative(startingUrl);
        } else {
            return getDefaultUrl();
        }
    }

    public static String getUrlFrom(String annotatedBaseUrl) {
        if (annotatedBaseUrl == null) {
            return null;
        }

        String classpathUrl = null;
        if (annotatedBaseUrl.startsWith(CLASSPATH_URL_PREFIX)) {
            URL baseUrl = obtainResourcePathFromClasspath(annotatedBaseUrl);
            classpathUrl = baseUrl.toString();
        }

        if (classpathUrl != null) {
            return classpathUrl;
        } else {
            return annotatedBaseUrl;
        }
    }

    private static URL obtainResourcePathFromClasspath(String annotatedBaseUrl) {
        String resourcePath = annotatedBaseUrl.substring(CLASSPATH_URL_PREFIX_LENGTH);
        URL baseUrl = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        if (baseUrl == null) {
            throw new IllegalStateException("No matching web page could be found on the classpath for "
                                            + annotatedBaseUrl);
        }
        return baseUrl;
    }

    private String getDefaultUrl() {
        if (pageLevelDefaultBaseUrl != null) {
            return pageLevelDefaultBaseUrl;
        } else {
            return PageConfiguration.getCurrentConfiguration().getBaseUrl();
        }
    }

    public String getStartingUrl(final String... parameterValues) {
        String startingUrlTemplate = getStartingUrl();
        return urlWithParametersSubstituted(startingUrlTemplate, parameterValues);
    }

    public String getNamedUrl(final String name) {
        NamedUrls urlAnnotation = pageObject.getClass().getAnnotation(NamedUrls.class);
        if (urlAnnotation != null) {
            NamedUrl[] namedUrlList = urlAnnotation.value();
            for (NamedUrl namedUrl : namedUrlList) {
                if (namedUrl.name().equals(name)) {
                    return namedUrl.url();
                }
            }
        }
        throw new IllegalArgumentException("No URL named " + name
                + " was found in this class");
    }

    public String getNamedUrl(final String name,
                              final String[] parameterValues) {
        String startingUrlTemplate = getNamedUrl(name);
        return urlWithParametersSubstituted(startingUrlTemplate, parameterValues);
    }

    private String urlWithParametersSubstituted(final String template,
                                                final String[] parameterValues) {

        String url = template;
        for (int i = 0; i < parameterValues.length; i++) {
            String variable = String.format("{%d}", i + 1);
            url = url.replace(variable, parameterValues[i]);
        }
        return addDefaultBaseUrlIfRelative(url);
    }

    private String addDefaultBaseUrlIfRelative(final String url) {
        if (isARelativeUrl(url)) {
            return getDefaultUrl() + url;
        } else {
            return url;
        }
    }

    private boolean isARelativeUrl(final String url) {
        return url.startsWith("/");
    }


    public void overrideDefaultBaseUrl(final String defaultBaseUrl) {
        pageLevelDefaultBaseUrl = defaultBaseUrl;
    }
}
