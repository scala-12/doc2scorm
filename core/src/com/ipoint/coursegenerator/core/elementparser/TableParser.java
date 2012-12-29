package com.ipoint.coursegenerator.core.elementparser;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TableParser {

    public static int parse(XWPFTable table, Document html,
	    XWPFDocument document, String path, HeaderInfo headerInfo,
	    ListParser listParser, Node parent) {
	Element htmlTable = html.createElement("table");
	for (int i = 0; i < table.getNumberOfRows(); i++) {
	    XWPFTableRow tableRow = table.getRow(i);
	    Element tr = html.createElement("tr");
	    for (int j = 0; j < tableRow.getTableCells().size(); j++) {
		XWPFTableCell tableCell = tableRow.getCell(j);
		Element td = html.createElement("td");
		if (tableCell.getCTTc().getTcPr().getVMerge() == null) {
		    if (tableCell.getCTTc().getTcPr().getGridSpan() != null) {
			td.setAttribute("colspan", tableCell.getCTTc()
				.getTcPr().getGridSpan().getVal().toString());
		    }
		    tr.appendChild(td);
		} else if (tableCell.getCTTc().getTcPr().getVMerge().getVal() == STMerge.RESTART) {
		    if (tableCell.getCTTc().getTcPr().getGridSpan() != null) {
			td.setAttribute("colspan", tableCell.getCTTc()
				.getTcPr().getGridSpan().getVal().toString());
		    }
		    int rowspan = 1;
		    for (int k = i + 1; k < table.getNumberOfRows(); k++) {
			if (table.getRow(k).getCell(j) != null
				&& table.getRow(k).getCell(j).getCTTc()
					.getTcPr().getVMerge() != null
				&& table.getRow(k).getCell(j).getCTTc()
					.getTcPr().getVMerge().getVal() != STMerge.RESTART) {
			    rowspan++;
			}
		    }
		    td.setAttribute("rowspan", String.valueOf(rowspan));
		    tr.appendChild(td);
		}
		for (int k = 0; k < tableCell.getParagraphs().size(); k++) {
		    IBodyElement bodyElement = tableCell.getBodyElements().get(
			    k);
		    if (bodyElement.getElementType().equals(
			    BodyElementType.PARAGRAPH)) {
			XWPFParagraph paragraph = (XWPFParagraph) bodyElement;
			if (paragraph.getNumID() != null) {
			    listParser.parse(paragraph, html, document,
				    headerInfo, path);
			} else {
			    ParagraphParser.parse(paragraph, html, document,
				    path, headerInfo, td);
			}
		    } else if (bodyElement.getElementType().equals(
			    BodyElementType.TABLE)) {
			XWPFTable nestedTable = (XWPFTable) bodyElement;
			TableParser.parse(nestedTable, html, document, path,
				headerInfo, listParser, td);
		    }
		}
	    }
	    htmlTable.appendChild(tr);
	}
	parent.appendChild(htmlTable);
	return 0;
    }

    public static int parse(Table hwpfTable, Document html,
	    HWPFDocument document, String path, HeaderInfo headerInfo,
	    ListParser listParser, Node parent) {
	int parCounter = 0;
	Element htmlTable = html.createElement("table");
	for (int j = 0; j < hwpfTable.numRows(); j++) {
	    TableRow row = hwpfTable.getRow(j);
	    Element tr = html.createElement("tr");
	    for (int k = 0; k < row.numCells(); k++) {
		TableCell cell = row.getCell(k);
		Element td = html.createElement("td");
		if (cell.isFirstVerticallyMerged()) {
		    int rowspan = 1;
		    for (int i = j + 1; i < hwpfTable.numRows(); i++) {
			if (hwpfTable.getRow(i).numCells() > k
				&& hwpfTable.getRow(i).getCell(k) != null
				&& hwpfTable.getRow(i).getCell(k)
					.isVerticallyMerged()
				&& !hwpfTable.getRow(i).getCell(k)
					.isFirstVerticallyMerged()) {
			    rowspan++;
			}
		    }
		    td.setAttribute("rowspan", String.valueOf(rowspan));
		    tr.appendChild(td);
		} else if (!cell.isVerticallyMerged()) {
		    tr.appendChild(td);
		}
		for (int i = 0; i < cell.numParagraphs(); i++) {
		    parCounter++;
		    Paragraph par = cell.getParagraph(i);
		    if (par.isInTable()
			    && par.getTableLevel() != hwpfTable.getTableLevel()
			    && !cell.getTable(par).toString()
				    .equals(hwpfTable.toString())) {
			i += TableParser.parse(cell.getTable(par), html,
				document, path, headerInfo, listParser, td);
		    } else if (par.isInList()) {
			listParser.parse(par, html, document, headerInfo, path);
		    } else {
			ParagraphParser.parse(par, html, document, path,
				headerInfo, td);
		    }
		}
	    }
	    htmlTable.appendChild(tr);
	}
	parent.appendChild(htmlTable);
	parCounter += hwpfTable.numRows() - 1;
	return parCounter;
    }
}