package net.thucydides.easyb;


import org.junit.Test
import net.thucydides.easyb.samples.SampleSteps
import net.thucydides.easyb.samples.MoreSampleSteps
import net.thucydides.easyb.samples.BigSampleLibrary

public class WhenDeterminingTheStepLibraryVariableName {


    @Test
    public void classnames_ending_in_Steps_drop_the_ending() {
        assert StepName.nameOf(SampleSteps.class) == "sample"
    }


    @Test
    public void complex_classnames_ending_in_Steps_should_be_underscored() {
        assert StepName.nameOf(MoreSampleSteps.class) == "more_sample"
    }

    @Test
    public void classnames_not_ending_in_Steps_should_be_underscored() {
        assert StepName.nameOf(BigSampleLibrary.class) == "big_sample_library"
    }

}
