package net.thucydides.core.matchers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BeanUniquenessMatcher implements BeanCollectionMatcher {

    private final String fieldName;
    
    public BeanUniquenessMatcher(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public <T> boolean matches(Collection<T> elements) {
        Set<Object> fieldValues = new HashSet<Object>();

        for(Object bean : elements) {
            fieldValues.add(BeanMatchers.getFieldValue(bean, fieldName));
        }
        return (fieldValues.size() == elements.size());
    }

    @Override
    public String toString() {
        return "each " + fieldName + " is different";
    }
}
