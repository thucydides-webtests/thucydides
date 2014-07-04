package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"cause", "suppressed"})
public abstract class JSONFailureCauseMixin {}
