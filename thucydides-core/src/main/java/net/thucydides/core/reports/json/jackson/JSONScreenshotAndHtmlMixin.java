package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public abstract class JSONScreenshotAndHtmlMixin {
    JSONScreenshotAndHtmlMixin(@JsonProperty("screenshot") File screenshot,
                               @JsonProperty("sourcecode") File sourcecode) {};


}