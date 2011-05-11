easybReport = new File("${basedir}/target/easyb/easyb.html")
assert easybReport.exists()

storiesXmlReport = new File("${basedir}/target/thucydides/testing_a_simple_site.xml")
assert storiesXmlReport.exists()

storiesHtmlReport = new File("${basedir}/target/thucydides/testing_a_simple_site.html")
assert storiesHtmlReport.exists()