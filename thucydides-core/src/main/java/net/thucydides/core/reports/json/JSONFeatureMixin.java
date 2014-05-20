package net.thucydides.core.reports.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JSONFeatureMixin {
    JSONFeatureMixin(@JsonProperty("id") String id,
                     @JsonProperty("name") final String name) {};

}
