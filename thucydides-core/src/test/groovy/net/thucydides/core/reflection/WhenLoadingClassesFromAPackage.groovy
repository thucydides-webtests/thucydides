package net.thucydides.core.reflection;

import net.thucydides.core.reflection.sampleclasses.SomeClass
import net.thucydides.core.reflection.sampleclasses.SomeOtherClass
import spock.lang.Specification
import net.thucydides.core.reflection.sampleclasses.SomeTestClass
import org.junit.runner.RunWith

public class WhenLoadingClassesFromAPackage extends Specification {

    def "should load all classes in a given package"() {
        when:
            List<Class> classes = ClassFinder.loadClasses().fromPackage("net.thucydides.core.reflection.sampleclasses");
        then:
            classes == [SomeClass, SomeOtherClass, SomeTestClass]

    }

    def "should load all classes in nested packages"() {
        when:
        List<Class> classes = ClassFinder.loadClasses().fromPackage("net.thucydides.core.reflection");
        then:
        classes.size() >= 3
    }

    def "should load no classes if the package does not exist"() {
        when:
        List<Class> classes = ClassFinder.loadClasses().fromPackage("that.does.not.exist");
        then:
        classes.isEmpty()
    }

    def "should not load resources on classpath"() {
        when:
        List<Class> classes = ClassFinder.loadClasses().fromPackage("jquery");
        then:
        classes.isEmpty()
    }

    def "should load classes with a given annotation"() {
        when:
            List<Class> classes = ClassFinder.loadClasses()
                                             .annotatedWith(RunWith)
                                             .fromPackage("net.thucydides.core.reflection.sampleclasses");
        then:
            classes == [SomeTestClass]
    }

}
