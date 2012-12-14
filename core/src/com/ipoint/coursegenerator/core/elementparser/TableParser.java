package com.ipoint.coursegenerator.core.elementparser;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFNum;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.ipoint.coursegenerator.core.elementparser.ParagraphParser;

public class TableParser {

    public static int parse(XWPFTable table, Document html, Object document,
	    String path, int headerLevel) {
	Element htmlTable = html.createElement("table");
	for (XWPFTableRow tableRow : table.getRows()) {
	    Element tr = html.createElement("tr");
	    for (XWPFTableCell tableCell : tableRow.getTableCells()) {
		Element td = html.createElement("td");
		if (tableCell != null) {
		    td.setTextContent(tableCell.getText());
		    tr.appendChild(td);
		}
	    }
	    htmlTable.appendChild(tr);
	}
	html.getElementsByTagName("body").item(0).appendChild(htmlTable);
	return 0;
    }

    public static int parse(Table hwpfTable, Document html, HWPFDocument document,
	    String path, int headerLevel) {
	int parCounter = 0;
	Element htmlTable = html.createElement("table");
	for (int j = 0; j < hwpfTable.numRows(); j++) {
	    TableRow row = hwpfTable.getRow(j);
	    Element tr = html.createElement("tr");
	    for (int k = 0; k < row.numCells(); k++) {
		TableCell cell = row.getCell(k);
		Element td = html.createElement("td");
		// System.out.println("[" + j + "][" + k + "]" + cell. +
		// "; "+ cell.isFirstMerged() + "; "+
		// cell.isFirstVerticallyMerged() + "; "+ cell.isMerged() +
		// "; "+ cell.isVertical() + "; "+ cell.isVerticallyMerged()
		// + "; ");
		for (int i = 0; i < cell.numParagraphs(); i++) {
		    parCounter++;
		    ParagraphParser.parse(cell.getParagraph(i), html, document,
			    path, headerLevel, td);
		}
		tr.appendChild(td);
	    }
	    htmlTable.appendChild(tr);
	}
	html.getElementsByTagName("body").item(0).appendChild(htmlTable);
	parCounter += hwpfTable.numRows() - 1;
	return parCounter;
    }
}