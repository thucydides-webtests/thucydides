// Verify story printing
htmlReport = new File("${basedir}/target/site/thucydides/index.html")
assert htmlReport.exists()

def htmlStoriesReport = htmlReport.text
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-123">MY-PROJECT-123</a>')
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-200">MY-PROJECT-200</a>')
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-300">MY-PROJECT-300</a>')
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-400">MY-PROJECT-400</a>')
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-456">MY-PROJECT-456</a>')

//def testReport = new File("${basedir}/target/site/thucydides/test_sample_test_scenario_happy_day_scenario.html").text
//
//assert testReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-123">#MY-PROJECT-123</a>')
//assert testReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-200">#MY-PROJECT-200</a>')
//assert testReport.contains("<a href=\"http://my.jira.server/browse/MY-PROJECT-300\">#MY-PROJECT-300</a>")
//assert testReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-456">#MY-PROJECT-456</a>')
//
//def failingTestReport = new File("${basedir}/target/site/thucydides/test_sample_test_scenario_failing_scenario.html").text
//assert failingTestReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-200">#MY-PROJECT-200</a>')
//assert failingTestReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-300">#MY-PROJECT-300</a>')
//assert failingTestReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-400">#MY-PROJECT-400</a>')
