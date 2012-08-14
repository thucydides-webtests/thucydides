package net.thucydides.easyb;


import net.thucydides.easyb.samples.BigSampleLibrary
import net.thucydides.easyb.samples.MoreSampleSteps
import net.thucydides.easyb.samples.SampleSteps
import org.junit.Test

public class WhenDeterminingTheStepLibraryVariableName {


    @Test
    public void classnames_ending_in_Steps_drop_the_ending() {
        assert StepName.defaultNameOf(SampleSteps.class) == "sample"
    }


    @Test
    public void complex_classnames_ending_in_Steps_should_be_underscored() {
        assert StepName.defaultNameOf(MoreSampleSteps.class) == "more_sample"
    }

    @Test
    public void classnames_not_ending_in_Steps_should_be_underscored() {
        assert StepName.defaultNameOf(BigSampleLibrary.class) == "big_sample_library"
    }

}
