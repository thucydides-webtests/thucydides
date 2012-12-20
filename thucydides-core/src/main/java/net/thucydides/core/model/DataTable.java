package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * A table of test data
 */
public class DataTable {
    final List<String> headers;
    final List<List<String>> rows;

    final static List<List<String>> NO_ROWS = Lists.newArrayList();

    DataTable(List<String> headers, List<List<String>> rows) {
        this.headers = headers;
        this.rows = rows;
    }

    public static DataTableBuilder withHeaders(List<String> headers) {
        return new DataTableBuilder(headers);
    }

    public List<String> getHeaders() {
        return ImmutableList.copyOf(headers);
    }

    public List<List<String>> getRows() {
        return ImmutableList.copyOf(rows);
    }

    public static class DataTableBuilder {
        final List<String> headers;
        final List<List<String>> rows;

        public DataTableBuilder(List<String> headers) {
            this(headers, NO_ROWS);
        }

        public DataTableBuilder(List<String> headers, List<List<String>> rows) {
            this.headers = headers;
            this.rows = rows;
        }

        public DataTable build() {
            return new DataTable(headers, rows);
        }

        public DataTableBuilder andRows(List<List<String>> rows) {
            return new DataTableBuilder(headers, rows);
        }

        public DataTableBuilder andMappedRows(List<Map<String,String>> mappedRows) {
            List<List<String>> rowData = Lists.newArrayList();
            for(Map<String,String> mappedRow : mappedRows) {
                rowData.add(rowDataFrom(mappedRow));
            }
            return new DataTableBuilder(headers, rowData);
        }

        private List<String> rowDataFrom(Map<String, String> mappedRow) {
            List<String> rowData = Lists.newArrayList();
            for(String header : headers) {
                rowData.add(mappedRow.get(header));
            }
            return rowData;
        }
    }
}
