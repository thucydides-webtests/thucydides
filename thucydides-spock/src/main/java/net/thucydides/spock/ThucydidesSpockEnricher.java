package net.thucydides.spock;

import org.spockframework.runtime.model.FieldInfo;
import org.spockframework.runtime.model.SpecInfo;

import java.util.List;

/**
 *
 */
public class ThucydidesSpockEnricher {

    private final SpecInfo specInfo;

    private ThucydidesSpockEnricher(SpecInfo specInfo) {
        this.specInfo = specInfo;
    }

    public static ThucydidesSpockEnricher forSpec(SpecInfo spec) {
        return new ThucydidesSpockEnricher(spec);
    }
}
