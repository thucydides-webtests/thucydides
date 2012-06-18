Given a stock of <symbol> and a threshold of <threshold>
When the stock is traded at <price>
Then the alert status should be <status>
 
Examples:     
|symbol|threshold|price|status|
|STK1|10.0|5.0|OFF|
|STK1|11.0|11.0|ON|
|STK1|12.0|12.0|ON|
