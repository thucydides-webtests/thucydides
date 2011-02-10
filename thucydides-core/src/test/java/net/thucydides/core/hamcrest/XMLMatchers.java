package net.thucydides.core.hamcrest;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class XMLMatchers {

    @Factory
    public static Matcher<String> isSimilarTo( String expectedDocment ) {
        return new XMLIsSimilarMatcher(expectedDocment);
    }
}
