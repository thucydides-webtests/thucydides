package net.thucydides.core.matchers;

import com.google.common.base.Preconditions;
import org.hamcrest.Matcher;

import java.util.Collection;

import static ch.lambdaj.Lambda.count;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.allOf;

public class BeanCountMatcher implements BeanCollectionMatcher {
    
    private final Matcher<Integer> countMatcher;

    public BeanCountMatcher(Matcher<Integer> countMatcher) {
        this.countMatcher = countMatcher;
    }

    public <T> boolean matches(Collection<T> elements) {
        return countMatcher.matches(elements.size());

    }

    @Override
    public String toString() {
        return  "number of matching entries " + countMatcher;
    }

    @Override
    public boolean matches(Object target) {
        return matches((Collection) target);
    }
}
