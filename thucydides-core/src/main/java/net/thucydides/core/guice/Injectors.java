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
        	throw new RuntimeException("Injector has not been initialized yet");
        }
        return injector;
    }
    
    public static synchronized Injector setInjector(Injector injector){
    	if (Injectors.injector == null) {
    		Injectors.injector = injector;
    	} else {
    		throw new IllegalStateException("injector is already set. Cannot reset it");
    	}
    	return injector;
    }
}
