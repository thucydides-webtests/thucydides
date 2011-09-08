package net.thucydides.core.steps;


import org.openqa.selenium.firefox.FirefoxDriver;

public class ConsoleStepListener extends BaseStepListener {

    private final StringBuffer buffer = new StringBuffer();

    int currentIndent = 0;

    public ConsoleStepListener() {
        super(FirefoxDriver.class, null);
    }

    @Override
    public String toString() {
        return buffer.toString();
    }

    private void push() {
        currentIndent++;
    }

    private void pop() {
        currentIndent--;
    }

    public void testStarted(String description) {
        buffer.append("TEST " + description).append("\n");
        push();
    }

    public void testFinished(TestStepResult result) {
        pop();
        buffer.append("TEST DONE").append("\n");
    }

    public void stepStarted(ExecutedStepDescription description) {
        writeIndent(buffer);
        buffer.append(description.getName()).append("\n");
        push();
    }

    private void writeIndent(StringBuffer buffer) {
        for(int i = 0; i < currentIndent; i++) {
            buffer.append("-");
        }
    }

    public void stepFinished() {
        pop();
        writeIndent(buffer);
        buffer.append("--> STEP DONE").append("\n");
    }

    public void stepFailed(StepFailure failure) {
        pop();
        writeIndent(buffer);
        buffer.append("--> STEP FAILED").append("\n");
    }

    public void stepIgnored() {
        pop();
        writeIndent(buffer);
        buffer.append("--> STEP IGNORED").append("\n");
    }

    public void stepPending() {
        pop();
        writeIndent(buffer);
        buffer.append("--> STEP PENDING").append("\n");
    }


    public void testFailed(Throwable cause) {
        buffer.append("--> TEST FAILED").append("\n");
    }

    public void testIgnored() {
        buffer.append("--> TEST IGNORED").append("\n");
    }
}
