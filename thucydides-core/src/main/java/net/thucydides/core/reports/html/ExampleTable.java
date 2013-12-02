package net.thucydides.core.reports.html;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Splitter;
import org.apache.commons.collections.IteratorUtils;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class ExampleTable {
    List<String> headers;
    List<List<String>> rows = Lists.newArrayList();

    final static Pattern NEW_LINE = Pattern.compile("(\\r\\n)|(\\n)|(\\r)");

    public ExampleTable(String unformattedTable) {
        String tableContents = unformattedTable.substring(unformattedTable.indexOf("|") + 1, unformattedTable.lastIndexOf("|"));

        Iterator<String> lineIter = Splitter.on(NEW_LINE).omitEmptyStrings().trimResults().split(tableContents).iterator();
        List<String> lines = IteratorUtils.toList(lineIter);
        addHeaderFrom(lines.get(0));
        for(int row = 1; row < lines.size(); row++) {
            addRowFrom(lines.get(row));
        }
    }

    private void addRowFrom(String row) {
        rows.add(cellsFrom(row));
    }

    private void addHeaderFrom(String headerLine) {
        headers = cellsFrom(headerLine);
    }

    private List<String> cellsFrom(String line) {
        line = line.trim();
        if (line.startsWith("|")) {
            line = line.substring(1);
        }
        if (line.endsWith("|")) {
            line = line.substring(0,line.length() - 1);
        }

        return IteratorUtils.toList(Splitter.on("|").trimResults().split(line).iterator());
    }

    public String inHtmlFormat() {
        return "<table class='embedded'>" + getHtmlHeader() + getHtmlBody() + "</table>";
    }

    public String getHtmlHeader() {
        StringBuffer htmlHeader = new StringBuffer();
        htmlHeader.append("<thead>");
        for(String header : headers) {
            htmlHeader.append("<th>").append(header).append("</th>");
        }
        htmlHeader.append("</thead>");
        return htmlHeader.toString();
    }

    public String getHtmlBody() {
        StringBuffer htmlBody = new StringBuffer();
        htmlBody.append("<tbody>");
        for(List<String> row : rows) {
            htmlBody.append("<tr>");
            for(String cell : row) {
                htmlBody.append("<td>").append(cell).append("</td>");
            }
            htmlBody.append("</tr>");
        }
        htmlBody.append("</tbody>");
        return htmlBody.toString();
    }

}
