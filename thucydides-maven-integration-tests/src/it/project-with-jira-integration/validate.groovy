// Verify story printing
htmlReport = new File("${basedir}/target/site/thucydides/e4cf909ced91d97f7c4907cfddc0f65e98708c3e4bc4d6c645b7168b598f760d.html")
assert htmlReport.exists()

def reportContent = htmlReport.text
assert reportContent.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-123">#123</a>')
assert reportContent.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-200">#200</a>')
assert reportContent.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-300">#300</a>')
assert reportContent.contains('<a target="_blank" href="http://my.jira.server/browse/MY-PROJECT-456">#456</a>')
