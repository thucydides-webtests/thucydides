package net.thucydides.core.reports.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JSONStoryMixin {
    @JsonIgnore public abstract String getName();
    @JsonIgnore public abstract String getId();
    @JsonIgnore public abstract String getReportName();
    @JsonIgnore public abstract String getFeature();
    @JsonIgnore public abstract String getFeatureClass();
    @JsonIgnore public abstract String getFeatureId();
}
