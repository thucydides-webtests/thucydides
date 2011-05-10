package net.thucydides.core.hamcrest;

import org.junit.Test;

import java.util.Arrays;

import static net.thucydides.core.hamcrest.XMLMatchers.isSimilarTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class WhenUsingTheXMLMatcher {

    @Test
    public void should_match_identical_xml_documents() {
        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";
        assertThat(anXmlDocument, isSimilarTo(anXmlDocument));
    }

    @Test
    public void should_not_match_different_xml_documents() {
        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";
        String aDifferentXmlDocument = "<loan><item code='a'>Item A</item></loan>";
        assertThat(anXmlDocument, not(isSimilarTo(aDifferentXmlDocument)));
    }

    @Test
    public void should_not_match_invalid_xml_document() {
        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";
        String aDifferentXmlDocument = "<loan><item code='a'>Item A</item><>";
        assertThat(anXmlDocument, not(isSimilarTo(aDifferentXmlDocument)));
    }

    @Test
    public void should_ignore_different_quotes_for_matches() {
        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";
        String anotherXmlDocument = "<sale><item code=\"a\">Item A</item></sale>";
        assertThat(anXmlDocument, isSimilarTo(anotherXmlDocument));
    }

    @Test
    public void should_ignore_white_space_for_matches() {
        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";
        String anotherXmlDocument = "<sale>  <item code='a'>Item A</item></sale>";
        assertThat(anXmlDocument, isSimilarTo(anotherXmlDocument));
    }
}
