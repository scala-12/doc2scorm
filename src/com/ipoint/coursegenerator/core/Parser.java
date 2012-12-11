package com.ipoint.coursegenerator.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.imsproject.xsd.imscpRootv1P1P2.ItemType;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestDocument;
import org.w3c.dom.Document;

import com.ipoint.coursegenerator.core.elementparser.ParagraphParser;
import com.ipoint.coursegenerator.core.elementparser.TableParser;
import com.ipoint.coursegenerator.core.utils.FileWork;
import com.ipoint.coursegenerator.core.utils.TransliterationTool;
import com.ipoint.coursegenerator.core.utils.Zipper;
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
	private String directoryPath;

	public ItemStyle(ItemType item, int styleId, String directoryPath) {
	    this.item = item;
	    this.styleId = styleId;
	    this.directoryPath = directoryPath;
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

	public String getDirectoryPath() {
	    return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
	    this.directoryPath = directoryPath;
	}
    }

    public Parser() {
    }

    public void createImsManifestFile(String courseName) {
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
	organizationProcessor.createOrganization(manifest.getManifest(),
		courseName);
	// Add Resources for Manifest
	ResourcesProcessor resourcesProcessor = new ResourcesProcessor();
	resourcesProcessor.createResources(manifest.getManifest());
    }

    private String tuneManifest(ManifestDocument manifestDocument) {
	String manifest = manifestDocument.xmlText();
	return manifest.replace("ims:", "").replace(PREFIX, PREFIX_NEW)
		.replace("datafromlms", "dataFromLMS")
		.replace("adl:", "adlcp:").replace("rootv1p2", "v1p3")
		.replace(":adl=", ":adlcp=");
    }

    public String parse(InputStream stream, String headerLevel,
	    String templateDir, String courseName, String path, String fileType)
	    throws IOException {
	createImsManifestFile(courseName);
	File directory = new File(path);
	if (directory.exists()) {
	    FileUtils.deleteDirectory(directory);
	}
	directory.mkdirs();
	Object doc = null;
	if (fileType.toLowerCase().equals(FILETYPE_DOC)) {
	    doc = new HWPFDocument(stream);
	} else if (fileType.toLowerCase().equals(FILETYPE_DOCX)) {
	    doc = new XWPFDocument(stream);
	}
	if (doc instanceof HWPFDocument) {
	    parseHWPF((HWPFDocument) doc, headerLevel, templateDir, courseName,
		    path);
	} else if (doc instanceof XWPFDocument) {
	    parseXWPF((XWPFDocument) doc, headerLevel, templateDir, courseName,
		    path);
	}
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
	File jsDir = new File(path + File.separator + "js");
	File cssDir = new File(path + File.separator + "css");
	if (!jsDir.exists()) {
	    jsDir.mkdirs();
	}
	if (!cssDir.exists()) {
	    cssDir.mkdirs();
	}
	FileUtils.copyFileToDirectory(new File(
		templateDir + File.separator + "parser.js"), jsDir);
	FileUtils.copyFileToDirectory(new File(
		templateDir + File.separator + "APIWrapper.js"), jsDir);
	FileUtils.copyFileToDirectory(new File(
		templateDir + File.separator + "SCOFunctions.js"), jsDir);
	FileUtils.copyFileToDirectory(new File(
		templateDir + File.separator + "kurs.css"), cssDir);
	String zipCourseFileName = getCourseZipFilename(courseName);
	Zipper zip = new Zipper(path + File.separator + zipCourseFileName,
		directory.getPath());
	zip.addToZip();
	return zipCourseFileName;
    }

    private void parseXWPF(XWPFDocument doc, String headerLevel,
	    String templateDir, String courseName, String path) {
	int headerLevelNumber = Integer.parseInt(headerLevel);
	Document html = null;
	int htmlFileCounter = 0;
	int paragraphStyle = 0;
	String fileName = null;
	ArrayList<ItemStyle> items = new ArrayList<Parser.ItemStyle>();
	for (IBodyElement bodyElement : doc.getBodyElements()) {
	    if (bodyElement.getElementType().equals(BodyElementType.PARAGRAPH)) {
		XWPFParagraph paragraph = (XWPFParagraph) bodyElement;
		if (paragraph.getStyleID() != null) {
		    try {
			if (ParagraphParser.isNumericParagraphStyle(paragraph.getStyleID())) {
			    paragraphStyle = Integer.valueOf(paragraph
				    .getStyleID());
			} else {
			    paragraphStyle = (int) 100;
			}
			System.out.println(paragraph.getStyle());
		    } catch (NumberFormatException e) {
			paragraphStyle = 100;
		    }
		    if (paragraphStyle > 0
			    && paragraphStyle <= headerLevelNumber) {
			if (html != null) {

			    String itemText = TransliterationTool
				    .convertRU2ENString(paragraph.getText());
			    itemText = itemText.replaceAll("[\\W&&[^-]]", "");
			    fileName = FileWork.HTML_PREFIX
				    + Integer.toString(htmlFileCounter) + "_"
				    + itemText + ".htm";
			    createItem(items, paragraph.getText(),
				    paragraphStyle, fileName, itemText);

			    FileWork.saveHTMLDocument(html, templateDir, path
				    + File.separator + FileWork.HTML_PREFIX
				    + Integer.toString(htmlFileCounter)
				    + ".htm", path);
			    htmlFileCounter++;
			}
			html = createNewHTMLDocument();
		    } else if (html == null) {
			html = createNewHTMLDocument();

		    }
		    ParagraphParser.parse(paragraph, html, doc, path,
			    headerLevelNumber);
		}

	    } else if (bodyElement.getElementType().equals(
		    BodyElementType.TABLE)) {
		XWPFTable table = (XWPFTable) bodyElement;
		TableParser.parse(table, html, doc, path, headerLevelNumber);

	    }

	}
	FileWork.saveHTMLDocument(html, templateDir, path + File.separator
		+ FileWork.HTML_PREFIX + Integer.toString(htmlFileCounter)
		+ ".htm", path);
    }

    private void parseHWPF(HWPFDocument document, String headerLevel,
	    String templateDir, String courseName, String path) {
	int headerLevelNumber = Integer.parseInt(headerLevel);
	String htmlPath = path;
	ArrayList<ItemStyle> items = new ArrayList<ItemStyle>();
	Range range = document.getRange();
	Document html = null;
	int htmlFileCounter = 0;
	String filename = null;
	String headerText = "";
	int headerStyleID = 0;
	boolean isFirstHeader = true;
	for (int i = 0; i < range.numParagraphs(); i++) {
	    Paragraph par = range.getParagraph(i);
	    boolean createItemForHeader = par.getStyleIndex() <= headerLevelNumber
		    && par.getStyleIndex() > 0;
	    String url = "";
	    if (html == null) {
		html = this.createNewHTMLDocument();
	    }

	    if (createItemForHeader
		    && !isFirstHeader
		    && i > 0
		    && range.getParagraph(i - 1).getStyleIndex() != par
			    .getStyleIndex()) {
		if (html != null) {
		    FileWork.saveHTMLDocument(html, templateDir, htmlPath
			    + File.separator + filename, path);
		    htmlFileCounter++;
		}
		html = this.createNewHTMLDocument();
	    }
	    String paragraphText = ParagraphParser.parse(par, html, document,
		    htmlPath, headerLevelNumber);
	    if (createItemForHeader) {
		if (i > 0
			&& range.getParagraph(i - 1).getStyleIndex() == par
				.getStyleIndex()) {
		    headerText += paragraphText;
		} else {
		    headerText = paragraphText;
		}
		headerStyleID = par.getStyleIndex();
	    }
	    if (createItemForHeader
		    && range.numParagraphs() > i + 1
		    && range.getParagraph(i + 1).getStyleIndex() != par
			    .getStyleIndex()) {
		isFirstHeader = false;
		headerText = headerText.length() > 127 ? headerText.substring(
			0, 127) : headerText;
		String itemText = TransliterationTool
			.convertRU2ENString(headerText);
		itemText = itemText.replaceAll(" ", "_");
		itemText = itemText.replaceAll("[\\W&&[^-]]", "");
		filename = FileWork.HTML_PREFIX
			+ Integer.toString(htmlFileCounter) + "_" + itemText
			+ ".htm";
		url = createItem(items, headerText, headerStyleID, filename,
			itemText).replace('/', File.separatorChar);
		htmlPath = path + File.separator + url;
		File f = new File(htmlPath);
		if (!f.exists()) {
		    f.mkdirs();
		}
	    }

	}
	if (filename != null) {
	    FileWork.saveHTMLDocument(html, templateDir, htmlPath
		    + File.separator + filename, path);
	}
    }

    public String createItem(ArrayList<ItemStyle> items, String scoName,
	    int styleIndex, String url, String itemText) {
	String path = "";
	ItemType parentItem = null;
	for (int j = items.size() - 1; j >= 0; j--) {
	    if (items.get(j).getStyleId() < styleIndex) {
		parentItem = items.get(j).getItem();
		path = items.get(j).getDirectoryPath();
		if (j == (items.size() - 1)) {
		    String resid = parentItem.getIdentifierref();
		    parentItem.getDomNode().getAttributes()
			    .removeNamedItem("identifierref");

		    OrganizationProcessor.createItem(parentItem,
			    parentItem.getTitle(), resid, "ITEM_"
				    + java.util.UUID.randomUUID().toString());
		}
		break;
	    }
	}
	String itemid = "ITEM_" + java.util.UUID.randomUUID().toString();
	String resid = "RES_" + java.util.UUID.randomUUID().toString();
	ItemType item = null;
	if (parentItem == null) {
	    item = OrganizationProcessor.createItem(manifest.getManifest()
		    .getOrganizations(), scoName, resid, itemid);
	} else {
	    item = OrganizationProcessor.createItem(parentItem, scoName, resid,
		    itemid);
	}
	ResourcesProcessor.createResource(manifest.getManifest(), url, resid);
	ItemStyle itemStyle = new ItemStyle(item, styleIndex, path
		+ File.separator + url.substring(0, url.lastIndexOf('.')));
	items.add(itemStyle);
	return path;
    }

    private String getCourseZipFilename(String courseName) {
	String zipCourseFileName = TransliterationTool
		.convertRU2ENString(courseName);
	Calendar date = GregorianCalendar.getInstance();
	zipCourseFileName = zipCourseFileName.replace(' ', '_');
	String sdate = "_"
		+ new Integer(date.get(GregorianCalendar.YEAR)).toString()
		+ "-"
		+ new Integer(date.get(GregorianCalendar.MONTH) + 1).toString()
		+ "-"
		+ new Integer(date.get(GregorianCalendar.DATE)).toString();
	zipCourseFileName = zipCourseFileName + sdate + ".zip";
	return zipCourseFileName;
    }
}
