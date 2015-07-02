package com.ipoint.coursegenerator.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlObject;
import org.imsproject.xsd.imscpRootv1P1P2.ItemType;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestDocument;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;
import org.imsproject.xsd.imscpRootv1P1P2.OrganizationsType;
import org.imsproject.xsd.imscpRootv1P1P2.ResourceType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.Course;
import com.ipoint.coursegenerator.core.courseModel.CourseTreeItem;
import com.ipoint.coursegenerator.core.newParser.CourseParser;
import com.ipoint.coursegenerator.core.utils.FileWork;
import com.ipoint.coursegenerator.core.utils.ImageInfo;
import com.ipoint.coursegenerator.core.utils.TransliterationTool;
import com.ipoint.coursegenerator.core.utils.Zipper;
import com.ipoint.coursegenerator.core.utils.manifest.ManifestProcessor;
import com.ipoint.coursegenerator.core.utils.manifest.MetadataProcessor;
import com.ipoint.coursegenerator.core.utils.manifest.OrganizationProcessor;
import com.ipoint.coursegenerator.core.utils.manifest.ResourcesProcessor;

public class Parser {

	public final static String FILETYPE_DOCX = ".docx";

	// public final static String FILETYPE_DOC = ".doc";

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
		OrganizationProcessor.createOrganization(manifest.getManifest(),
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

	private static Document createNewHTMLDocument() {
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

	private ItemType addOrganizationElementToManifest(XmlObject parentItem,
			CourseTreeItem node) {
		String id = java.util.UUID.randomUUID().toString();
		String itemId = "ITEM_".concat(id);
		String resourseId = (node.getPage() == null) ? null : "RES_".concat(id);

		ItemType manifestItem = null;
		if (parentItem instanceof ItemType) {
			manifestItem = OrganizationProcessor.createItem(
					(ItemType) parentItem, node.getTitle(), resourseId, itemId);
		} else if (parentItem instanceof OrganizationsType) {
			manifestItem = OrganizationProcessor.createItem(
					(OrganizationsType) parentItem, node.getTitle(),
					resourseId, itemId);
		}

		return manifestItem;
	}

	private void addResourceToManifest(ManifestType manifest, String path,
			String pageName, List<ImageInfo> images, String resourseId) {
		ResourceType itemResource = ResourcesProcessor.createResource(manifest,
				path.concat(pageName).concat(FileWork.HTML_SUFFIX), resourseId);

		ArrayList<String> imagesNames = new ArrayList<String>(); 
		for (ImageInfo image : images) {
			imagesNames.add(image.getImageName());
		}
		
		ResourcesProcessor.addFilesToResource(
				path.concat("img").concat(File.separator), itemResource,
				imagesNames);
	}

	private void addPageToCourse(CourseTreeItem node, String templateDir,
			String coursePath, String pagePath, String pageName) {
		Document html = createNewHTMLDocument();
		Element pageDiv = node.toHtml(html);
		html.getElementsByTagName("body").item(0).appendChild(pageDiv);

		boolean pageAdded = FileWork.saveHTMLDocument(html, templateDir, coursePath,
				pagePath, pageName.concat(FileWork.HTML_SUFFIX));
		if (pageAdded) {
			FileWork.saveImagesOfPage(node.getPage().getImages(), coursePath.concat(pagePath).concat(FileWork.IMAGE_PATH));
		}
	}

	private void recursiveSaveCourse(List<CourseTreeItem> items,
			ManifestType manifest, String templateDir, String coursePath,
			String path, String level, XmlObject parentItem) {
		int numberOnLevel = 0;
		String part = (level == null) ? FileWork.HTML_PREFIX
				: FileWork.HTML_PREFIX.concat(level).concat("-");
		for (CourseTreeItem item : items) {
			// title of page
			// page name with level number
			String pageTitle = part.concat(String.valueOf(++numberOnLevel))
					.concat("_");
			pageTitle = pageTitle.concat(TransliterationTool
					.convertRU2ENString(item.getTitle()));
			pageTitle = pageTitle.replaceAll(" ", "_");
			pageTitle = pageTitle.replaceAll("[\\W&&[^-]]", "");

			ItemType manifestItem = addOrganizationElementToManifest(
					parentItem, item);
			if (item.getPage() != null) {
				this.addPageToCourse(item, templateDir, coursePath, path,
						pageTitle);
				this.addResourceToManifest(manifest, path, pageTitle, item
						.getPage().getImages(), manifestItem
						.getIdentifierref());

			}

			if (!item.getCourseTree().isEmpty()) {
				this.recursiveSaveCourse(
						item.getCourseTree(),
						manifest,
						templateDir,
						coursePath,
						path.concat(pageTitle).concat(File.separator),
						(level == null) ? String.valueOf(numberOnLevel) : level
								.concat("-").concat(
										String.valueOf(numberOnLevel)),
						manifestItem);
			}
		}
	}

	public String parse(InputStream stream, String headerLevel,
			String templateDir, String courseName, String path, String fileType)
			throws IOException {
		File directory = new File(path);
		if (directory.exists()) {
			FileUtils.deleteDirectory(directory);
		}
		directory.mkdirs();
		XWPFDocument doc = new XWPFDocument(stream);
		Course courseModel = CourseParser.parse((XWPFDocument) doc, courseName);

		// TODO: replace "%name_of_course%" on "courseModel.getTitle()"
		this.createImsManifestFile("%name_of_course%");

		if (!courseModel.getCourseTree().isEmpty()) {
			this.recursiveSaveCourse(courseModel.getCourseTree(), manifest
					.getManifest(), templateDir, path.concat(File.separator),
					"", null, manifest.getManifest().getOrganizations());
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

}