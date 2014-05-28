package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.thucydides.core.model.DataTableRow;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonIgnoreProperties({"size"})
@JsonInclude(NON_EMPTY)
public abstract class JSONDataTableMixin {
    JSONDataTableMixin(@JsonProperty("headers") List<String> headers,
                       @JsonProperty("rows")List<DataTableRow> rows) {};

}
