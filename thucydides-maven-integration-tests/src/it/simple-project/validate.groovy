htmlReport = new File("${basedir}/target/site/thucydides/index.html")
assert htmlReport.exists()

storiesReport = new File("${basedir}/target/site/thucydides/stories.html")
assert storiesReport.exists()

def testReport = new File("${basedir}/target/site/thucydides/features.html").text
assert testReport.contains("<div class=\"percentagebar\" title=\"50% pending\">")
assert testReport.contains("<div class=\"failingbar\" style=\"width: 0px;\"  title=\"0% failing\">")
assert testReport.contains("<div class=\"passingbar\" style=\"width: 75px;\" title=\"50% passing\">")
