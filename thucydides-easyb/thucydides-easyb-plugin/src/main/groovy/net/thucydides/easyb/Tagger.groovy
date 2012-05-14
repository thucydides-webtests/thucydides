package net.thucydides.easyb

import net.thucydides.core.util.Inflector

class Tagger {
    private final PluginConfiguration pluginConfiguration;

    Tagger(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration
    }

    @Override
    Object invokeMethod(String methodName, Object args) {
        if (args[0] instanceof String) {
            def tagType = methodName
            addTagOrIssue(tagType, args)
        } else {
            return super.invokeMethod(methodName, args)
        }
    }

    def addTagOrIssue(String type, Object args) {
        if ((type == 'issue') || (type == 'issues')) {
            addIssues(args)
        }
        else {
            addTags(type, args)
            pluginConfiguration.tag(args[0], type)
        }
    }

    def addIssues(Object args) {
        args.each { issue ->
            pluginConfiguration.tests_issue(issue)
        }
    }

    def addTags(String type, Object[] args) {
        def tagType = Inflector.instance.of(type).inSingularForm().toString()
        args.each { tag ->
            pluginConfiguration.tag(tag, tagType)
        }
    }

}
