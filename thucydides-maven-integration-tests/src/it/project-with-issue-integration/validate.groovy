// Verify story printing
htmlReport = new File("${basedir}/target/site/thucydides/cc01122f4ddb558d18f0f05ea292c168afddbf7625b150b76aeafc62af4dde64.html")
assert htmlReport.exists()
def reportContent = htmlReport.text
assert reportContent.contains("<a target=\"_blank\" href=\"http://my.jira.server/browse/MYPROJECT-123\">#123</a> and <a target=\"_blank\" href=\"http://my.jira.server/browse/MYPROJECT-456\">#456</a>")
