import net.thucydides.easyb.samples.SampleSteps

using "thucydides"

thucydides.uses_steps_from SampleSteps

scenario "Use some sample steps", {
    given "we have a step library", {}
    when "we invoke a step", {
        sample.step1()
    }
    and "some more steps are called", {
        sample.step2()
        sample.step3()
    }
    then "nested steps should work too", {
        sample.nestedSteps()
    }
}
