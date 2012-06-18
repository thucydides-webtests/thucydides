Meta:
@issue MYPROJ-1, MYPROJ-2

Scenario: A scenario that works
Meta:
@issues MYPROJ-3,MYPROJ-4
@issue MYPROJ-5

Given I have an implemented JBehave scenario
And the scenario works
When I run the scenario
Then I should get a successful result

Scenario: Another scenario that works
Meta:
@issues MYPROJ-6,MYPROJ-7

Given I have an implemented JBehave scenario
And the scenario works
When I run the scenario
Then I should get a successful result