package net.thucydides.core.matchers;

import ch.lambdaj.function.convert.Converter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;

class BeanUniquenessMatcher implements BeanCollectionMatcher {

    private final String fieldName;
    
    public BeanUniquenessMatcher(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public boolean matches(Object target) {
        return matches((Collection) target);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> boolean matches(Collection<T> elements) {
        List<Object> allFieldValues = convert(elements, new FieldValueExtractor());
        Set<Object> uniquefieldValues = new HashSet<Object>();

        uniquefieldValues.addAll(allFieldValues);

        return (uniquefieldValues.size() == elements.size());
    }

    public class FieldValueExtractor implements Converter<Object, Object> {
        @Override
        public Object convert(Object from) {
            return BeanMatchers.getFieldValue(from, fieldName);
        }
    }

    @Override
    public String toString() {
        return "each " + fieldName + " is different";
    }

}
