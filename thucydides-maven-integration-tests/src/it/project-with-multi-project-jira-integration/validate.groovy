// Verify story printing
htmlReport = new File("${basedir}/target/site/thucydides/cc01122f4ddb558d18f0f05ea292c168afddbf7625b150b76aeafc62af4dde64.html")
assert htmlReport.exists()

def htmlStoriesReport = htmlReport.text
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-123">MY-PROJECT-123</a>')
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-200">MY-PROJECT-200</a>')
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-300">MY-PROJECT-300</a>')
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-456">MY-PROJECT-456</a>')
