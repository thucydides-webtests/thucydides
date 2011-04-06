package net.thucydides.easyb

import org.easyb.plugin.EasybPlugin
import javax.imageio.spi.ServiceRegistry;

public class ThucydidesExtensions {

    public PluginConfiguration getThucydides() {
        def configuration = getLocalThucydidesPlugin().getConfiguration();
        println "Configuration: " + configuration;
        return configuration;
    }

    private ThucydidesPlugin getLocalThucydidesPlugin() {
        Iterator providers = ServiceRegistry.lookupProviders(EasybPlugin.class, ClassLoader.getSystemClassLoader())
        def found = false
        ThucydidesPlugin plugin;
        while (providers.hasNext()) {
            Object provider = providers.next()
            if (provider instanceof ThucydidesPlugin) {
                println "Plugin found: " + provider;
                plugin = (ThucydidesPlugin) provider;
            }
        }
        if (plugin != null) {
            return plugin;
        }
        throw new IllegalStateException("No Thucydides Plugin was found for this story.")
    }
}
