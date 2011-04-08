package net.thucydides.core.pages;

import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;

class NamedUrlSet {
    private Object pageObject;

    public NamedUrlSet(final Object pageObject) {
        this.pageObject = pageObject;
    }

    public String getNamedUrl(final String name) {
        NamedUrls urlAnnotation = getClass().getAnnotation(NamedUrls.class);
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

}
