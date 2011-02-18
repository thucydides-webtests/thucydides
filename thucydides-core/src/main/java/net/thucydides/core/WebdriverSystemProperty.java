package net.thucydides.core;

/**
 * Properties that can be passed to a web driver test to customize its behaviour.
 * This class is mainly for internal use.
 * 
 * @author johnsmart
 *
 */
public enum WebdriverSystemProperty {

    DRIVER("webdriver.driver"),
    BASE_URL("webdriver.base.url");

    private String propertyName;

    private WebdriverSystemProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

}
