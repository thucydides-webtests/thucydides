package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonIgnoreProperties({"error","successful","failure","ignored","pending","skipped","screenshotCount",
                       "durationInSeconds", "flattenedSteps", "leafTestSteps", "agroup",
                       "errorMessage","shortErrorMessage"})
@JsonInclude(NON_NULL)
public abstract class JSONTestStepMixin {}
