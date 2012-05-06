package net.thucydides.easyb

class Tagger {
    private final PluginConfiguration pluginConfiguration;

    Tagger(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration
    }

    @Override
    Object invokeMethod(String name, Object args) {
        if (args[0] instanceof String) {
            pluginConfiguration.tag(name, args[0])
        } else {
            return super.invokeMethod(name, args)
        }
    }
}
