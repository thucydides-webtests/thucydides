package net.thucydides.plugins.jira.workflow

import org.codehaus.groovy.control.CompilerConfiguration
import net.thucydides.core.model.TestResult
import net.thucydides.plugins.jira.workflow.TransitionBuilder.TransitionSetMap

/**
 * Manage JIRA workflow integration.
 * JIRA workflow integration is configured using a simple Groovy DSL to define the transitionSetMap to be performed
 * for each test result.
 */
class Workflow {

    public static final String WORKFLOW_CONFIGURATION_PROPERTY = "thucydides.jira.workflow"

    private final String name;
    private final boolean active;

    def builder = new TransitionBuilder()

    protected Workflow(String name, String configuration, boolean active) {
        this.name = name;
        this.active = active;
        Script s = new GroovyClassLoader().parseClass(configuration).newInstance()
        s.binding = new BuilderBinding(builder:builder)
        s.run()
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public TransitionSetMap getTransitions() {
        builder.getTransitionSetMap()
    }
}

class BuilderBinding extends Binding {
    def builder
    Object getVariable(String name) {
        return { Object... args ->  builder.invokeMethod(name,args) }
    }
}