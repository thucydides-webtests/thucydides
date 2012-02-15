package net.thucydides.core.output;

import com.google.common.collect.ImmutableList;
import jxl.Cell;
import jxl.JXLException;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.BoldStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import net.thucydides.core.matchers.SimpleValueMatcher;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SpreadsheetResultsOutput implements ResultsOutput {
    List<String> titles;
    File outputFile;

    public SpreadsheetResultsOutput(File outputFile, List<String> titles) {
        this.titles = ImmutableList.copyOf(titles);
        this.outputFile = outputFile;

    }

    @Override
    public synchronized void recordResult(SimpleValueMatcher check, List<String> columnValues) throws IOException {

        WritableWorkbook workbook = null;

        try {
            workbook = obtainWorkbook();
            WritableSheet sheet = workbook.getSheet(0);

            boolean isAFailedTest = !check.matches();
            WritableCellFormat font = getFontFor(isAFailedTest);

            int row = sheet.getRows();
            int column = 0;
            for (String columnValue : columnValues) {
                Label resultCell = new Label(column++, row, columnValue, font);
                sheet.addCell(resultCell);
            }
            workbook.write();
        } catch (JXLException e) {
            throw new IOException(e);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (WriteException e) {
                }
            }
        }
    }

    private WritableCellFormat getFontFor(boolean aFailedTest) throws WriteException {
        WritableFont baseFont = new WritableFont(WritableFont.ARIAL, 10);
        if (aFailedTest) {
            baseFont.setBoldStyle(WritableFont.BOLD);
            baseFont.setColour(Colour.RED);
        }
        return new WritableCellFormat(baseFont);
    }

    private WritableWorkbook obtainWorkbook() throws IOException, BiffException {
        WritableWorkbook workbook;
        if (!outputFile.exists()) {
            workbook = createNewSpreadSheet(outputFile);
        } else {
            workbook = openExistingSpreadsheet();
        }
        return workbook;
    }

    private WritableWorkbook openExistingSpreadsheet() throws BiffException, IOException {
        return Workbook.createWorkbook(outputFile, Workbook.getWorkbook(outputFile));
    }

    private WritableWorkbook createNewSpreadSheet(File outputFile) throws IOException {
        try {
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));

            WritableWorkbook workbook = Workbook.createWorkbook(outputFile, wbSettings);
            workbook.createSheet("Test Results", 0);
            WritableSheet sheet = workbook.getSheet(0);

            int cellIndex = 0;
            for (String title : titles) {
                Label label = new Label(cellIndex++, 0, title);
                sheet.addCell(label);
            }
            return workbook;
        } catch (RowsExceededException e) {
            throw new IOException(e);
        } catch (WriteException e) {
            throw new IOException(e);
        }
    }
}
