// Verify story printing
htmlReport = new File("${basedir}/target/site/thucydides/index.html")
assert htmlReport.exists()

def reportContent = htmlReport.text

assert reportContent.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-123">#123</a>')
assert reportContent.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-200">#200</a>')
assert reportContent.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-300">#300</a>')
assert reportContent.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-400">#400</a>')
assert reportContent.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-456">#456</a>')
