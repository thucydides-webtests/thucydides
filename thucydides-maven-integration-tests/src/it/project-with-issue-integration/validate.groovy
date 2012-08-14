// Verify story printing
htmlReport = new File("${basedir}/target/site/thucydides/index.html")
assert htmlReport.exists()
def reportContent = htmlReport.text
assert reportContent.contains("<a target=\"_blank\" href=\"http://my.jira.server/browse/MYPROJECT-123\">#123</a> and <a target=\"_blank\" href=\"http://my.jira.server/browse/MYPROJECT-456\">#456</a>")
