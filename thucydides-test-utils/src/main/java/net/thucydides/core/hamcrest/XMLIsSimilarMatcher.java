package net.thucydides.core.hamcrest;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Compare XML structures.
 */
public class XMLIsSimilarMatcher extends TypeSafeMatcher<String> {
    
    private String xmlDocument;
    
    private String errorMessage = "";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLIsSimilarMatcher.class);

    public XMLIsSimilarMatcher(final String xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    public boolean matchesSafely(final String expectedXML) {
        
        boolean xmlIsSimilar = true;
        Diff difference = null;
        try {
            XMLUnit.setIgnoreAttributeOrder(true);
            XMLUnit.setIgnoreComments(true);
            XMLUnit.setIgnoreWhitespace(true);
            difference = new Diff(xmlDocument,expectedXML);
            xmlIsSimilar = difference.similar();
        } catch (Exception e) {
            xmlIsSimilar = false; 
            LOGGER.info(e.getMessage());
        }
        if (!xmlIsSimilar) {
            recordErrorMessage(difference);
        }
        return xmlIsSimilar;
    }

    private void recordErrorMessage(final Diff difference) {
        StringBuffer buffer = new StringBuffer();
        if (difference != null) {
            buffer = difference.appendMessage(buffer);
        }
        errorMessage = buffer.toString();
    }

    public void describeTo(final Description description) {
        description.appendText("an XML document equivalent to ").appendText(xmlDocument);
        if ((errorMessage != null) && (!errorMessage.isEmpty())){
            description.appendText("[").appendText(errorMessage).appendText("]");
        }
    }
}
