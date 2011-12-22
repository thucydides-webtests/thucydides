package net.thucydides.plugins.jira.workflow

public interface WorkflowLoader {
    Workflow load();
    String getDefaultWorkflow();
}
