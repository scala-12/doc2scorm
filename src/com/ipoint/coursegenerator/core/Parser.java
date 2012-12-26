package com.ipoint.coursegenerator.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestDocument;
import org.w3c.dom.Document;

import com.ipoint.coursegenerator.core.elementparser.HeaderFinder;
import com.ipoint.coursegenerator.core.elementparser.HeaderInfo;
import com.ipoint.coursegenerator.core.elementparser.ItemInfo;
import com.ipoint.coursegenerator.core.elementparser.ListParser;
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

    private static final String PREFIX = "<manifest identifier=\"SingleSharableResource_MulitipleFileManifest\" version=\"1.1\" xmlns:ims=\"http://www.imsproject.org/xsd/imscp_rootv1p1p2\"><metadata/>";
    private static final String PREFIX_NEW = "<manifest xmlns=\"http://www.imsglobal.org/xsd/imscp_v1p1\" xmlns:imsmd=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:adlcp=\"http://www.adlnet.org/xsd/adlcp_v1p3\" xmlns:imsss=\"http://www.imsglobal.org/xsd/imsss\" xmlns:adlseq=\"http://www.adlnet.org/xsd/adlseq_v1p3\" xmlns:adlnav=\"http://www.adlnet.org/xsd/adlnav_v1p3\" identifier=\"MANIFEST-5724A1B2-A6BE-F1BF-9781-706050DA4FC9\" xsi:schemaLocation=\"http://www.imsglobal.org/xsd/imscp_v1p1 imscp_v1p1.xsd http://ltsc.ieee.org/xsd/LOM lom.xsd http://www.adlnet.org/xsd/adlcp_v1p3 adlcp_v1p3.xsd http://www.imsglobal.org/xsd/imsss imsss_v1p0.xsd http://www.adlnet.org/xsd/adlseq_v1p3 adlseq_v1p3.xsd http://www.adlnet.org/xsd/adlnav_v1p3 adlnav_v1p3.xsd\"><metadata><schema>ADL SCORM</schema><schemaversion>2004 4th Edition</schemaversion></metadata>";

    private ManifestDocument manifest;

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
	FileUtils.copyFileToDirectory(new File(templateDir + File.separator
		+ "parser.js"), jsDir);
	FileUtils.copyFileToDirectory(new File(templateDir + File.separator
		+ "APIWrapper.js"), jsDir);
	FileUtils.copyFileToDirectory(new File(templateDir + File.separator
		+ "SCOFunctions.js"), jsDir);
	FileUtils.copyFileToDirectory(new File(templateDir + File.separator
		+ "kurs.css"), cssDir);
	String zipCourseFileName = getCourseZipFilename(courseName);
	Zipper zip = new Zipper(path + File.separator + zipCourseFileName,
		directory.getPath());
	zip.addToZip(new String[] { zipCourseFileName });
	return zipCourseFileName;
    }

    private void parseXWPF(XWPFDocument doc, String headerLevel,
	    String templateDir, String courseName, String path) {
	Document html = null;
	int paragraphStyle = 0;
	ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();
	ItemInfo firstItem = new ItemInfo(null, 999, "", "", "");
	firstItem.setPath(path);
	firstItem.setHtmlPath(path);
	items.add(firstItem);
	int nextParagraphStyle = 0;
	HeaderInfo headerInfo = new HeaderInfo(Integer.parseInt(headerLevel));
	headerInfo.setTemplateDir(templateDir);
	ListParser listParser = new ListParser();
	for (int i = 0; i < doc.getBodyElements().size(); i++) {
	    IBodyElement bodyElement = doc.getBodyElements().get(i);
	    if (bodyElement.getElementType().equals(BodyElementType.PARAGRAPH)) {
		XWPFParagraph paragraph = (XWPFParagraph) bodyElement;
		if (paragraph.getStyleID() != null) {
		    doc.getStyles().getStyle(paragraph.getStyleID())
			    .getCTStyle();
		}
		paragraphStyle = getCurrentParagraphStyleID(paragraph);
		if (doc.getBodyElements().size() > i + 1
			&& doc.getBodyElements().get(i + 1) instanceof XWPFParagraph
			&& ((XWPFParagraph) doc.getBodyElements().get(i + 1))
				.getStyleID() != null) {
		    String nextParagraphStyleID = null;
		    if (((XWPFParagraph) doc.getBodyElements().get(i + 1))
			    .getStyleID() != null) {
			nextParagraphStyleID = ((XWPFParagraph) doc
				.getBodyElements().get(i + 1)).getStyleID();
		    } else {
			nextParagraphStyleID = "100";
		    }
		    nextParagraphStyle = getNextParagraphStyleID(nextParagraphStyleID);
		} else {
		    nextParagraphStyle = 100;
		}
		if (paragraph.getNumID() != null
			&& !isHeading(paragraphStyle,
				headerInfo.getHeaderLevelNumber())) {
		    listParser.parse(paragraph, html, doc, headerInfo, path);
		    headerInfo.setPreviousParStyleID(paragraphStyle);
		} else {
		    headerInfo.setNextParStyleID(nextParagraphStyle);
		    html = HeaderFinder.parse(paragraph, html, headerInfo,
			    items, doc, manifest.getManifest(), paragraphStyle);
		    listParser.reset();
		}
	    } else if (bodyElement.getElementType().equals(
		    BodyElementType.TABLE)) {
		XWPFTable table = (XWPFTable) bodyElement;
		TableParser.parse(table, html, doc, path,
			headerInfo.getHeaderLevelNumber());
	    }
	}
	if (items.get(items.size() - 1).getFilename() != null) {
	    ItemInfo itemInfo = items.get(items.size() - 1);
	    FileWork.saveHTMLDocument(html, templateDir, itemInfo.getHtmlPath()
		    + File.separator + itemInfo.getFilename(), path);
	    ResourcesProcessor.addFilesToResource(itemInfo.getUrl(),
		    itemInfo.getResource(), headerInfo.getPathToResources());
	}
    }

    private void parseHWPF(HWPFDocument document, String headerLevel,
	    String templateDir, String courseName, String path) {
	ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();
	ItemInfo firstItem = new ItemInfo(null, 999, "", "", "");
	firstItem.setPath(path);
	firstItem.setHtmlPath(path);
	items.add(firstItem);
	Range range = document.getRange();
	Document html = null;
	HeaderInfo headerInfo = new HeaderInfo(Integer.parseInt(headerLevel));
	headerInfo.setTemplateDir(templateDir);
	ListParser listParser = new ListParser();
	for (int i = 0; i < range.numParagraphs(); i++) {
	    Paragraph par = range.getParagraph(i);
	    if (par.isInTable()) {
		i += TableParser.parse(range.getTable(par), html, document,
			items.get(items.size() - 1).getHtmlPath(), headerInfo);
	    } else if (par.isInList()
		    && !isHeading(par.getStyleIndex(),
			    headerInfo.getHeaderLevelNumber())) {
		listParser.parse(par, html, document, headerInfo,
			items.get(items.size() - 1).getHtmlPath());
		headerInfo.setPreviousParStyleID(par.getStyleIndex());
	    } else {
		if (i < range.numParagraphs() - 1) {
		    headerInfo.setNextParStyleID(range.getParagraph(i + 1)
			    .getStyleIndex());
		} else {
		    headerInfo.setNextParStyleID(0);
		}
		String htmlHash = "";
		if (html != null) {
		    htmlHash = html.toString();
		}
		html = HeaderFinder.parse(par, html, headerInfo, items,
			document, manifest.getManifest(), par.getStyleIndex());
		if (!html.toString().equals(htmlHash))
		    listParser.reset();
	    }
	}
	if (items.get(items.size() - 1).getFilename() != null) {
	    ItemInfo itemInfo = items.get(items.size() - 1);
	    FileWork.saveHTMLDocument(html, templateDir, itemInfo.getHtmlPath()
		    + File.separator + itemInfo.getFilename(), path);
	    ResourcesProcessor.addFilesToResource(itemInfo.getUrl(),
		    itemInfo.getResource(), headerInfo.getPathToResources());
	}
    }

    private String getCourseZipFilename(String courseName) {
	String zipCourseFileName = TransliterationTool
		.convertRU2ENString(courseName);
	Calendar date = GregorianCalendar.getInstance();
	zipCourseFileName = zipCourseFileName.replace(' ', '_').replaceAll(
		"[\\W&&[^-]]", "");
	;
	String sdate = "_"
		+ new Integer(date.get(GregorianCalendar.YEAR)).toString()
		+ "-"
		+ new Integer(date.get(GregorianCalendar.MONTH) + 1).toString()
		+ "-"
		+ new Integer(date.get(GregorianCalendar.DATE)).toString();
	zipCourseFileName = zipCourseFileName + sdate + ".zip";
	return zipCourseFileName;
    }

    public static boolean isHeading(int parStyleId, int headerLevel) {
	return parStyleId <= headerLevel && parStyleId > 0;
    }

    public static int getCurrentParagraphStyleID(XWPFParagraph xwpfParagraph) {
	int resultVariable = 0;
	try {
	    if (xwpfParagraph.getStyleID() != null) {
		if (ParagraphParser.isNumericParagraphStyle(xwpfParagraph
			.getStyleID())) {
		    resultVariable = Integer
			    .valueOf(xwpfParagraph.getStyleID());
		    if (resultVariable == 20) {
			resultVariable = (int) 2;
		    }
		} else {
		    resultVariable = getNonNumericHeaderParser(xwpfParagraph
			    .getStyleID());
		}
	    } else {
		resultVariable = (int) 100;
	    }
	} catch (NumberFormatException e) {
	    resultVariable = (int) 100;
	}
	return resultVariable;
    }

    public int getNextParagraphStyleID(String strStyleID) {
	int resultVariable = 0;
	try {
	    if (ParagraphParser.isNumericParagraphStyle(strStyleID)) {
		resultVariable = Integer.valueOf(strStyleID);
		if (resultVariable == 20) {
		    resultVariable = (int) 2;
		}
	    } else {
		resultVariable = getNonNumericHeaderParser(strStyleID);
	    }
	} catch (NumberFormatException e) {
	    resultVariable = (int) 100;
	}
	return resultVariable;
    }

    public static int getNonNumericHeaderParser(String headId) {
	int resultVariable = 0;
	if (headId.equals("Heading1")) {
	    resultVariable = 1;
	} else if (headId.equals("Heading2")) {
	    resultVariable = 2;
	} else if (headId.equals("Heading3")) {
	    resultVariable = 3;
	} else if (headId.equals("Heading4")) {
	    resultVariable = 4;
	} else if (headId.equals("Heading5")) {
	    resultVariable = 5;
	} else if (headId.equals("Heading6")) {
	    resultVariable = 6;
	} else if (headId.equals("Heading7")) {
	    resultVariable = 7;
	} else if (headId.equals("Heading8")) {
	    resultVariable = 8;
	} else if (headId.equals("Heading9")) {
	    resultVariable = 9;
	}
	return resultVariable;
    }
}