package net.thucydides.core.junit.rules;

import java.util.HashMap;
import java.util.Map;

import net.thucydides.core.WebdriverSystemProperty;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Conserves the webdriver-related system properties (webdriver.*).
 * If they were defined, they will be restored to their old values.
 * If they where not defined before a test, they will be deleted.
 * @author johnsmart
 *
 */
public class SaveWebdriverSystemPropertiesRule implements MethodRule {
    
    private static final Map<String,String> ORIGINAL_WEB_DRIVER_PROPERTY_VALUES = new HashMap<String,String>();
    {
        for (WebdriverSystemProperty property : WebdriverSystemProperty.values()) {
            savePropertyValueFor(property);
        }                        
    }
    
    private static void savePropertyValueFor(final WebdriverSystemProperty property) {
        String propertyName = property.getPropertyName();
        String currentValue = System.getProperty(propertyName);
        if (currentValue != null) {
            ORIGINAL_WEB_DRIVER_PROPERTY_VALUES.put(propertyName, currentValue);
        }
    }
    
    public Statement apply(final Statement statement, final FrameworkMethod method, final Object target) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                } catch(Throwable exception) {
                    throw exception;
                } finally {
                    restoreOldSystemProperties();
                }
            }

            private void restoreOldSystemProperties() {
                
                for (WebdriverSystemProperty property : WebdriverSystemProperty.values()) {
                    restorePropertyValueFor(property);
                }                        
            }

            private void restorePropertyValueFor(final WebdriverSystemProperty property) {
                String propertyName = property.getPropertyName();
                String originalValue = ORIGINAL_WEB_DRIVER_PROPERTY_VALUES.get(propertyName);
                if (originalValue != null) {                        
                    System.setProperty(propertyName, originalValue);
                } else {
                    System.clearProperty(propertyName);
                }
            }
        };
    }    
}
