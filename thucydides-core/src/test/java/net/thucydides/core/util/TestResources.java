package net.thucydides.core.util;

import java.io.File;

public class TestResources {
    public static File directoryInClasspathCalled(final String resourceName) {
        return new File(TestResources.class.getResource(resourceName).getPath());
    }

    public static File fileInClasspathCalled(final String resourceName) {
        return new File(TestResources.class.getResource(resourceName).getPath());
    }

}
