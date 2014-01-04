package net.thucydides.easyb

import org.easyb.plugin.EasybPlugin

import javax.imageio.spi.ServiceRegistry

public class ThucydidesExtensions {

    private ThucydidesPlugin plugin = null

    public PluginConfiguration getThucydides() {
        def configuration = getPlugin().getConfiguration();
        return configuration;
    }


    private ThucydidesPlugin getPlugin() {
        if (plugin == null) {
            plugin = getLocalThucydidesPlugin()
        }
        return plugin
    }

    private ThucydidesPlugin getLocalThucydidesPlugin() {

        Iterator providers = ServiceRegistry.lookupProviders(EasybPlugin, ClassLoader.getSystemClassLoader())

        def registeredPlugin = null
        providers.each { provider ->
            if (provider instanceof ThucydidesPlugin) {
                registeredPlugin = provider;
            }
        }

        if (registeredPlugin) {
            return registeredPlugin
        }
        throw new IllegalStateException("No Thucydides Plugin was found for this story.")
    }
}
