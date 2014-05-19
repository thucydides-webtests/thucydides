package net.thucydides.core.reports.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@JsonIgnoreProperties({"name","id","reportName","feature","featureClass","featureId"})
@JsonInclude(NON_NULL)
public abstract class JSONTestStepMixin {}
