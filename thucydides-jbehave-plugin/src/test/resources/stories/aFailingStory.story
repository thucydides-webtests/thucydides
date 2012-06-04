Scenario: A scenario that fails

Given I have an implemented JBehave scenario
And the scenario fails
When I run the scenario
Then I should get a failed result