// Verify story printing
htmlReport = new File("${basedir}/target/site/thucydides/index.html")
assert htmlReport.exists()

htmlStoriesReport = new File("${basedir}/target/site/thucydides/stories.html")
assert htmlStoriesReport.exists()

def testReport = new File("${basedir}/target/site/thucydides/sample_test_scenario_happy_day_scenario.html").text
assert testReport.contains("<a href=\"http://my.jira.server/browse/MYPROJECT-123\">#123</a>")
assert testReport.contains("<a href=\"http://my.jira.server/browse/MYPROJECT-456\">#456</a>")
assert testReport.contains("<a href=\"http://my.jira.server/browse/MYPROJECT-200\">#200</a>")
assert testReport.contains("<a href=\"http://my.jira.server/browse/MYPROJECT-300\">#300</a>")

def failingTestReport = new File("${basedir}/target/site/thucydides/sample_test_scenario_failing_scenario.html").text
assert failingTestReport.contains("<a href=\"http://my.jira.server/browse/MYPROJECT-200\">#200</a>")
assert failingTestReport.contains("<a href=\"http://my.jira.server/browse/MYPROJECT-300\">#300</a>")
assert failingTestReport.contains("<a href=\"http://my.jira.server/browse/MYPROJECT-400\">#400</a>")

def storiesReportContent = htmlStoriesReport.text
assert storiesReportContent.contains("<a href=\"http://my.jira.server/browse/MYPROJECT-123\">#123</a>")
