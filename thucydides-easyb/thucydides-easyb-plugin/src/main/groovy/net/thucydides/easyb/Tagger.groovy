package net.thucydides.easyb

class Tagger {
    private final PluginConfiguration pluginConfiguration;

    Tagger(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration
    }

    @Override
    Object invokeMethod(String methodName, Object args) {
        if (args[0] instanceof String) {
            def tagType = methodName
            pluginConfiguration.tag(args[0], tagType)
        } else {
            return super.invokeMethod(methodName, args)
        }
    }
}
