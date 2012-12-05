package com.ipoint.coursegenerator.core;

import java.io.InputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


import java.util.List;
//import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.htmlparser.util.ParserException;
import org.imsproject.xsd.imscpRootv1P1P2.ItemType;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.w3c.dom.Document;

import com.ipoint.coursegenerator.core.elementparser.ParagraphParser;
import com.ipoint.coursegenerator.core.utils.FileUtils;
import com.ipoint.coursegenerator.core.utils.manifest.ManifestProcessor;
import com.ipoint.coursegenerator.core.utils.manifest.MetadataProcessor;
import com.ipoint.coursegenerator.core.utils.manifest.OrganizationProcessor;
import com.ipoint.coursegenerator.core.utils.manifest.ResourcesProcessor;

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


    private static final String PREFIX = "<manifest identifier=\"SingleSharableResource_MulitipleFileManifest\" version=\"1.1\" xmlns:ims=\"http://www.imsproject.org/xsd/imscp_rootv1p1p2\"><metadata/>";
    private static final String PREFIX_NEW = "<manifest xmlns=\"http://www.imsglobal.org/xsd/imscp_v1p1\" xmlns:imsmd=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:adlcp=\"http://www.adlnet.org/xsd/adlcp_v1p3\" xmlns:imsss=\"http://www.imsglobal.org/xsd/imsss\" xmlns:adlseq=\"http://www.adlnet.org/xsd/adlseq_v1p3\" xmlns:adlnav=\"http://www.adlnet.org/xsd/adlnav_v1p3\" identifier=\"MANIFEST-5724A1B2-A6BE-F1BF-9781-706050DA4FC9\" xsi:schemaLocation=\"http://www.imsglobal.org/xsd/imscp_v1p1 imscp_v1p1.xsd http://ltsc.ieee.org/xsd/LOM lom.xsd http://www.adlnet.org/xsd/adlcp_v1p3 adlcp_v1p3.xsd http://www.imsglobal.org/xsd/imsss imsss_v1p0.xsd http://www.adlnet.org/xsd/adlseq_v1p3 adlseq_v1p3.xsd http://www.adlnet.org/xsd/adlnav_v1p3 adlnav_v1p3.xsd\"><metadata><schema>ADL SCORM</schema><schemaversion>2004 4th Edition</schemaversion></metadata>";
 
    private ManifestDocument manifest;

    private class ItemStyle {
	ItemType item;
	int styleId;

	public ItemStyle(ItemType item, int styleId) {
	    this.item = item;
	    this.styleId = styleId;
	}

	public ItemType getItem() {
	    return item;
	}

	public int getStyleId() {
	    return styleId;
	}

	public void setItem(ItemType item) {
	    this.item = item;
	}

	public void setStyleId(int styleId) {
	    this.styleId = styleId;
	}
    }

    public Parser() {
	createImsManifestFile();
    }

    public void createImsManifestFile() {
	manifest = ManifestDocument.Factory.newInstance();

	ManifestDocument.Factory.newInstance();
	// Create Manifest for Manifest Document
	ManifestProcessor manifestProcessor = new ManifestProcessor();

	manifestProcessor.createManifest(manifest);

	// Add Metadata for Manifest
	MetadataProcessor metadataProcessor = new MetadataProcessor();
	metadataProcessor.createMetadata(manifest.getManifest());
	// Add Organization (default and root) to Manifest
	OrganizationProcessor organizationProcessor = new OrganizationProcessor();
	organizationProcessor.createOrganization(manifest.getManifest());
	// Add Resources for Manifest
	ResourcesProcessor resourcesProcessor = new ResourcesProcessor();
	try {
	    resourcesProcessor.createResources(manifest.getManifest());
	} catch (UnsupportedEncodingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ParserException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private String tuneManifest(ManifestDocument manifestDocument) {
	String manifest = manifestDocument.xmlText();
	return manifest.replace("ims:", "").replace(PREFIX, PREFIX_NEW)
		.replace("datafromlms", "dataFromLMS")
		.replace("adl:", "adlcp:").replace("rootv1p2", "v1p3")
		.replace(":adl=", ":adlcp=");
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
	int headerLevelNumber = Integer.parseInt(headerLevel);
	String level = "";
	XWPFDocument document = doc;
	Document html = null;
	int htmlFileCounter = 0;
	int paragraphStyle = 0;
	final List<IBodyElement> bodyElements = document.getBodyElements();

	for (int i = 0; i < bodyElements.size(); i++) {
	    if (bodyElements.get(i).getElementType()
		    .equals(BodyElementType.PARAGRAPH)) {
		XWPFParagraph paragraph = (XWPFParagraph) bodyElements.get(i);
		if (paragraph.getStyle() != null) {
		    try {
			paragraphStyle = Integer.valueOf(paragraph.getStyleID());
			System.out.println(paragraph.getStyle());
		    } catch (NumberFormatException e) {		paragraphStyle = 0;
		    }
		    if (paragraphStyle > 0
			    && paragraphStyle <= headerLevelNumber) {
			if (html != null) {
			    FileUtils.saveHTMLDocument(html, path
				    + File.separator + FileUtils.HTML_PREFIX
				    + Integer.toString(htmlFileCounter)
				    + ".htm");
			}
			html = createNewHTMLDocument();
		    } else if (html == null) {
			html = createNewHTMLDocument();

		    }
		    ParagraphParser.parse(paragraph, html, document, path);
		}
		
	    } else if (bodyElements.get(i).getElementType()
		    .equals(BodyElementType.TABLE)) {

	    }

	}
	FileUtils.saveHTMLDocument(html, path + File.separator
		+ FileUtils.HTML_PREFIX + Integer.toString(htmlFileCounter)
		+ ".htm");
    }

    private void parseHWPF(HWPFDocument doc, String headerLevel,
	    String template, String courseName, String path) {
	int headerLevelNumber = Integer.parseInt(headerLevel);
	ArrayList<ItemStyle> items = new ArrayList<ItemStyle>();
	HWPFDocument document = doc;
	Range range = document.getRange();
	Document html = null;
	int htmlFileCounter = 0;
	for (int i = 0; i < range.numParagraphs(); i++) {
	    Paragraph par = range.getParagraph(i);
	    if (par.getStyleIndex() <= headerLevelNumber
		    && par.getStyleIndex() > 0) {
		if (html != null) {
		    ItemType parentItem = null;
		    for (int j = items.size() - 1; j >= 0; j--) {
			if (items.get(j).getStyleId() > par.getStyleIndex()) {
			    parentItem = items.get(j).getItem();
			    break;
			}
		    }
		    ItemType item = null;
		    if (parentItem == null) {
			OrganizationProcessor.createItem(manifest.getManifest()
				.getOrganizations(), par.text(), "");
		    } else {
			OrganizationProcessor.createItem(parentItem, "", "");
		    }
		    ItemStyle itemStyle = new ItemStyle(item,
			    par.getStyleIndex());
		    items.add(itemStyle);

		    FileUtils.saveHTMLDocument(html,
			    path + File.separator + FileUtils.HTML_PREFIX
				    + Integer.toString(htmlFileCounter)
				    + ".htm");
		}
		html = this.createNewHTMLDocument();
	    } else if (html == null) {
		html = this.createNewHTMLDocument();
	    }
	    ParagraphParser.parse(par, html, document, path, headerLevelNumber);
	}
	FileUtils.saveHTMLDocument(html, path + File.separator
		+ FileUtils.HTML_PREFIX + Integer.toString(htmlFileCounter)
		+ ".htm");

	String res = tuneManifest(manifest);
	File f = new File(path, "imsmanifest.xml");
	try {
	    f.createNewFile();

	    FileWriter fr = new FileWriter(f);
	    fr.write(res);
	    fr.close();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
