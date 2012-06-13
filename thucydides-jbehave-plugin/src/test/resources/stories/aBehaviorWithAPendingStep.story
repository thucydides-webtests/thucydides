Scenario: A scenario with a pending step

Given I have an implemented JBehave scenario
And the scenario has steps
When I run the scenario
And one of the steps is pending
Then the test outcome should be pending