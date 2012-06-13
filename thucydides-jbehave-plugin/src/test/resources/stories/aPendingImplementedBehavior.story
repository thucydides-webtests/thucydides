Scenario: A scenario with implemented pending steps

Given a JBehave story with a pending implementation
When the story is executed
Then the steps should be marked as pending