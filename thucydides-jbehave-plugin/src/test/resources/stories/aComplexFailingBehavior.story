Scenario: A scenario that fails

Given I have a badly implemented JBehave scenario
And the scenario fails
When I run the scenario
Then I should get a failed result
And I should skip subsequent results