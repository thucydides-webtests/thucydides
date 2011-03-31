package net.thucydides.core.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;

class PageUrls {

    private final PageObject pageObject;

    private String defaultBaseUrl;

    public PageUrls(final PageObject pageObject) {
        this.pageObject = pageObject;
    }

    public String getUrlWithParametersSubstituted(final String template, final String[] parameterValues) {

        String url = template;
        for (int i = 0; i < parameterValues.length; i++) {
            String variable = String.format("{%d}", i + 1);
            url = url.replace(variable, parameterValues[i]);
        }
        return addDefaultBaseUrlIfRelative(url);
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
        throw new IllegalArgumentException("No URL named " + name + " was found in this class");
    }


    public String getStartingUrl() {
        DefaultUrl urlAnnotation = pageObject.getClass().getAnnotation(DefaultUrl.class);
        if (urlAnnotation != null) {
            String annotatedBaseUrl = urlAnnotation.value();
            return addDefaultBaseUrlIfRelative(annotatedBaseUrl);
        }

        return getDefaultBaseUrl();
    }

    private String addDefaultBaseUrlIfRelative(final String url) {
        if (isARelativeUrl(url)) {
            return getDefaultBaseUrl() + url;
        } else {
            return url;
        }
    }

    private boolean isARelativeUrl(final String url) {
        return url.startsWith("/");
    }

    public void setDefaultBaseUrl(final String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    public String getDefaultBaseUrl() {
        if (defaultBaseUrl == null) {
            return PageConfiguration.getCurrentConfiguration().getBaseUrl();
        } else {
            return defaultBaseUrl;
        }
    }


}
