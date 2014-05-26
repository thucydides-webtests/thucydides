package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JSONScreenshotAndHtmlMixin {
    JSONScreenshotAndHtmlMixin(@JsonProperty("name") String id,
                               @JsonProperty("type") final String name) {};

}
