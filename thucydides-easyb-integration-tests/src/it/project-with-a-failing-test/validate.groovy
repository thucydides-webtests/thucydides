easybReport = new File("${basedir}/target/easyb/easyb.html")
assert easybReport.exists()

// Check that tests after the failing test are skipped

//def xmlResults =  new File("${basedir}/target/thucydides/testingasitewithafailure_triggering_a_failure.xml").text
//def acceptanceTestRuns = new XmlParser().parseText(xmlResults)

//assert acceptanceTestRuns."test-step"[0].attributes().result == "SUCCESS"
//assert acceptanceTestRuns."test-step"[1].attributes().result == "FAILURE"
//assert acceptanceTestRuns."test-step"[2].attributes().result == "SKIPPED"
