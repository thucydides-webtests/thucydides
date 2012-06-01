import org.jbehave.core.annotations.Then

Scenario: A scenario with pending steps

Given JBehave story with no implementation
When the story is executed
Then the steps should be marked as pending
And sample implementations should be proposed