package net.thucydides.core.model;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.lambdaj.Lambda.convert;

/**
 * A table of test data
 */
public class DataTable {
    private final List<String> headers;
    private final List<DataTableRow> rows;
    private final boolean predefinedRows;
    private AtomicInteger currentRow = new AtomicInteger(0);

    private final static List<DataTableRow> NO_ROWS = Lists.newArrayList();

    private DataTable(List<String> headers, List<DataTableRow> rows) {
        this.headers = headers;
        this.rows = new CopyOnWriteArrayList(rows);
        this.predefinedRows = !rows.isEmpty();
    }

    public static DataTableBuilder withHeaders(List<String> headers) {
        return new DataTableBuilder(headers);
    }

    public List<String> getHeaders() {
        return ImmutableList.copyOf(headers);
    }

    public List<DataTableRow> getRows() {
        return ImmutableList.copyOf(rows);
    }

    public RowValueAccessor row(int rowNumber) {
        return new RowValueAccessor(this, rowNumber);
    }

    public RowValueAccessor nextRow() {
        return new RowValueAccessor(this, nextRowNumber());
    }

    public boolean atLastRow() {
        return ((rows.isEmpty()) || (currentRow.get() == rows.size() - 1));
    }

    public RowValueAccessor currentRow() {
        return new RowValueAccessor(this, currentRowNumber());
    }

    private int nextRowNumber() {
        return currentRow.incrementAndGet();
    }

    private int currentRowNumber() {
        return currentRow.intValue();
    }

    public void addRow(Map<String, String> data) {
        DataTableRow newRow = new DataTableRow(ImmutableList.copyOf(data.values()));
        rows.add(newRow);
        currentRow.set(rows.size() - 1);
    }

    public boolean hasPredefinedRows() {
        return predefinedRows;
    }

    public static class DataTableBuilder {
        final List<String> headers;
        final List<DataTableRow> rows;

        public DataTableBuilder(List<String> headers) {
            this(headers, NO_ROWS);
        }

        public DataTableBuilder(List<String> headers, List<DataTableRow> rows) {
            this.headers = headers;
            this.rows = rows;
        }

        public DataTable build() {
            return new DataTable(headers, rows);
        }

        public DataTableBuilder andRows(List<List<String>> rows) {
            return new DataTableBuilder(headers, convert(rows, toDataTableRows()));
        }

        public DataTableBuilder andRowData(List<DataTableRow> rows) {
            return new DataTableBuilder(headers, rows);
        }

        public DataTableBuilder andMappedRows(List<Map<String,String>> mappedRows) {
            List<List<String>> rowData = Lists.newArrayList();
            for(Map<String,String> mappedRow : mappedRows) {
                rowData.add(rowDataFrom(mappedRow));
            }
            return new DataTableBuilder(headers, convert(rowData, toDataTableRows()));
        }

        private Converter<List<String>, DataTableRow> toDataTableRows() {
            return new Converter<List<String>, DataTableRow>() {

                public DataTableRow convert(List<String> cellValues) {
                    return new DataTableRow(cellValues);
                }
            };
        }

        private List<String> rowDataFrom(Map<String, String> mappedRow) {
            List<String> rowData = Lists.newArrayList();
            for(String header : headers) {
                rowData.add(mappedRow.get(header));
            }
            return rowData;
        }
    }

    public class RowValueAccessor {
        private final DataTable dataTable;
        private final int rowNumber;
        public RowValueAccessor(DataTable dataTable, int rowNumber) {
            this.dataTable = dataTable;
            this.rowNumber = rowNumber;
        }

        public void hasResult(TestResult result) {
            dataTable.rows.get(rowNumber).updateResult(result);
        }
    }
}
