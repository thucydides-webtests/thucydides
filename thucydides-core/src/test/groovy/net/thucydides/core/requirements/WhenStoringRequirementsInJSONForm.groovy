package net.thucydides.core.requirements

import com.github.goldin.spock.extensions.tempdir.TempDir
import spock.lang.Specification

class WhenStoringRequirementsInJSONForm extends Specification {

    @TempDir
    File outputDirectory

    def requirements = new ChildrenFirstOrderedMap()
    def child1 = new Req("child 1","Child",1);
    def child2 = new Req("child 2","Child",1);
    def child3 = new Req("child 3","Child",1);

    def "should be able to save and load Requirements as JSON files"() {
        given:
            def capability1 = new Req(0,"parent","Parent","CARD-1","capability","A Parent", [child1, child2])
            def capability2 = new Req(0,"parent","Parent","CARD-1","capability","A Parent", [child3])
            requirements.put("capability1", capability1)
            requirements.put("capability2", capability2)
            requirements.put("capability1.child1", child1)
            requirements.put("capability1.child2", child2)
            requirements.put("capability2.child3", child3)
            def persister = new RequirementPersister(outputDirectory,"root")
        when:
            persister.write(requirements);
            def requirementsReloaded = persister.read()
        then:
            requirementsReloaded.get("capability1")
        and:
            requirementsReloaded.get("capability1") == capability1
            requirementsReloaded.get("capability2") == capability2
    }
}