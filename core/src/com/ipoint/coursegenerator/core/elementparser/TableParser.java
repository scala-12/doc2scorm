package com.ipoint.coursegenerator.core.elementparser;

import org.apache.poi.hwpf.HWPFDocument;
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
			if (paragraph.getStyleID() != null) {
			    document.getStyles()
				    .getStyle(paragraph.getStyleID())
				    .getCTStyle();
			}
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
		System.out.println("[" + j + "][" + k + "]" + cell.text()
			+ "; " + cell.isFirstMerged() + "; "
			+ cell.isFirstVerticallyMerged() + "; "
			+ cell.isMerged() + "; " + cell.isVerticallyMerged()
			+ "; ");
		for (int i = 0; i < cell.numParagraphs(); i++) {
		    parCounter++;
		    ParagraphParser.parse(cell.getParagraph(i), html, document,
			    path, headerInfo, td);
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