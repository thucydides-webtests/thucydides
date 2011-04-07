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
        Iterator providers = ServiceRegistry.lookupProviders(EasybPlugin, ClassLoader.getSystemClassLoader())

        ThucydidesPlugin plugin

        providers.each { provider ->
            if (provider instanceof ThucydidesPlugin) {
                plugin = (ThucydidesPlugin) provider;
            }
        }

        if (plugin) {
            return plugin
        }
        throw new IllegalStateException("No Thucydides Plugin was found for this story.")
    }
}
