package net.thucydides.core.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Somewhere to hold the Guice injector.
 * There might be a better way to do this.
 */
public class Injectors {

    private static Injector injector;

    public static synchronized Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(new ThucydidesModule());
        }
        return injector;
    }
}
