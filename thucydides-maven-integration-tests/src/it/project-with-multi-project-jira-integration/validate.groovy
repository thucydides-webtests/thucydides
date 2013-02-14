// Verify story printing
htmlReport = new File("${basedir}/target/site/thucydides/e4cf909ced91d97f7c4907cfddc0f65e98708c3e4bc4d6c645b7168b598f760d.html")
assert htmlReport.exists()

def htmlStoriesReport = htmlReport.text
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-123">MY-PROJECT-123</a>')
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-200">MY-PROJECT-200</a>')
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-300">MY-PROJECT-300</a>')
assert htmlStoriesReport.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-456">MY-PROJECT-456</a>')
