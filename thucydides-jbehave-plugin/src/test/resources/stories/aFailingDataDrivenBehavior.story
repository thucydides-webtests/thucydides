package stories
Given a stock of <symbol> and a threshold of <threshold>
When the stock is traded at <price>
Then the alert status should be <status>
And some other stuff should also work
 
Examples:     
|symbol|threshold|price|status|
|STK2|10.0|5.0|OFF|
|STK2|11.0|11.0|FAIL|
|STK2|12.0|12.0|ON|
