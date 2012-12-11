package com.ipoint.coursegenerator.core.elementparser;

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

    public static void parse(XWPFTable table, Document html, Object document,
	    String path, int headerLevel) {
	if (table instanceof XWPFTable) {  
	    Element htmlTable = html.createElement("table");
	    htmlTable.setAttribute("border", "1");
	    for (XWPFTableRow tableRow : table.getRows()) {
		Element tr = html.createElement("tr");				
		for (XWPFTableCell tableCell : tableRow.getTableCells()) {		
		    Element td = html.createElement("td");
		    if(tableCell != null) {
		    td.setTextContent(tableCell.getText());		
		    tr.appendChild(td);
		    }
		}
		htmlTable.appendChild(tr);		
	    }
	    html.getElementsByTagName("body").item(0).appendChild(htmlTable);
	} 
    }
}