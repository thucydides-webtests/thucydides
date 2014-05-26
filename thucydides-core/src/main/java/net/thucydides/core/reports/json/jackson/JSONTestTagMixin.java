package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JSONTestTagMixin {
    JSONTestTagMixin(@JsonProperty("id") String id,
                     @JsonProperty("name") final String name) {};

}
