// Verify story printing
htmlReport = new File("${basedir}/target/site/thucydides/index.html")
assert htmlReport.exists()

def reportContent = htmlReport.text

assert reportContent.contains('<a href="http://my.jira.server/browse/MY-PROJECT-123">#123</a>')
assert reportContent.contains('<a href="http://my.jira.server/browse/MY-PROJECT-200">#200</a>')
assert reportContent.contains('<a href="http://my.jira.server/browse/MY-PROJECT-300">#300</a>')
assert reportContent.contains('<a href="http://my.jira.server/browse/MY-PROJECT-400">#400</a>')
assert reportContent.contains('<a href="http://my.jira.server/browse/MY-PROJECT-456">#456</a>')

def testReport = new File("${basedir}/target/site/thucydides/sample_test_scenario_happy_day_scenario.html").text

assert testReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-123">#123</a>')
assert testReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-200">#200</a>')
assert testReport.contains("<a href=\"http://my.jira.server/browse/MY-PROJECT-300\">#300</a>")
assert testReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-456">#456</a>')

def failingTestReport = new File("${basedir}/target/site/thucydides/sample_test_scenario_failing_scenario.html").text
assert failingTestReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-200">#200</a>')
assert failingTestReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-300">#300</a>')
assert failingTestReport.contains('<a href="http://my.jira.server/browse/MY-PROJECT-400">#400</a>')
