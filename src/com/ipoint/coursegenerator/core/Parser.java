package com.ipoint.coursegenerator.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

import com.ipoint.coursegenerator.core.elementparser.ParagraphParser;
import com.ipoint.coursegenerator.core.utils.FileUtils;

public class Parser {
    
    public final static String FILETYPE_DOCX = ".docx";

    public final static String FILETYPE_DOC = ".doc";
    
    private Document createNewHTMLDocument() {
	try {
	    Document html = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder().newDocument();
	    html.appendChild(html.createElement("html"));
	    html.getFirstChild().appendChild(html.createElement("head"));
	    html.getFirstChild().appendChild(html.createElement("body"));
	    return html;
	} catch (ParserConfigurationException e) {

	}
	return null;
    }

    public Parser() {
    }

    public void parse(InputStream stream, String headerLevel, String template,
	    String courseName, String path, String fileType) throws IOException {
	File directory = new File(path);
	directory.mkdirs();
	Object doc = null;
	if (fileType.toLowerCase().equals(FILETYPE_DOC)) {
	    doc = new HWPFDocument(stream);	    
	} else if (fileType.toLowerCase().equals(FILETYPE_DOCX)) {
	    doc = new XWPFDocument(stream);
	}
	if (doc instanceof HWPFDocument) {
	    parseHWPF((HWPFDocument) doc, headerLevel, template, courseName,
		    path);
	} else if (doc instanceof XWPFDocument) {
	    parseXWPF((XWPFDocument) doc, headerLevel, template, courseName,
		    path);
	}
    }

    private void parseXWPF(XWPFDocument doc, String headerLevel,
	    String template, String courseName, String path) {

    }

    private void parseHWPF(HWPFDocument doc, String headerLevel,
	    String template, String courseName, String path) {
	int headerLevelNumber = Integer.parseInt(headerLevel);
	HWPFDocument document = doc;
	Range range = document.getRange();
	Document html = null;
	int htmlFileCounter = 0;
	for (int i = 0; i < range.numParagraphs(); i++) {
	    Paragraph par = range.getParagraph(i);
	    if (par.getStyleIndex() <= headerLevelNumber
		    && par.getStyleIndex() > 0) {
		if (html != null) {
		    FileUtils.saveHTMLDocument(html,
			    path + File.separator + FileUtils.HTML_PREFIX
				    + Integer.toString(htmlFileCounter)
				    + ".htm");
		}
		html = this.createNewHTMLDocument();
	    } else if (html == null) {
		html = this.createNewHTMLDocument();
	    }
	    ParagraphParser.parse(par, html, document, path);
	}
	FileUtils.saveHTMLDocument(html, path + File.separator
		+ FileUtils.HTML_PREFIX + Integer.toString(htmlFileCounter)
		+ ".htm");
    }
}
