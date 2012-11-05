package net.thucydides.core.matchers;

import com.google.common.base.Optional;
import net.thucydides.core.reflection.FieldValue;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.closeTo;

public class BigDecimalValueMatcher {
    private final Number value;
    private final Matcher<? extends Object> matcher;

    protected BigDecimalValueMatcher(Number value, Matcher<? extends BigDecimal> matcher) {
        this.value = value;
        Object expectedValue = expectedValue(matcher);
        this.matcher = closeTo(new BigDecimal(expectedValue.toString()), new BigDecimal("0"));
    }

    private Object expectedValue(Matcher matcher) {
        if (matcher.getClass() == Is.class) {
            Matcher innerMatcher = (Matcher) FieldValue.inObject(matcher).fromFieldNamed("matcher").get();
            Optional<Object> fieldValue = FieldValue.inObject(innerMatcher).fromFieldNamed("expectedValue");
            return fieldValue.orNull();
        } else {
            Optional<Object> fieldValue = FieldValue.inObject(matcher).fromFieldNamed("expectedValue");
            return fieldValue.orNull();
        }
    }

    public boolean matches() {
        return matcher.matches(new BigDecimal(value.toString()));
    }
}