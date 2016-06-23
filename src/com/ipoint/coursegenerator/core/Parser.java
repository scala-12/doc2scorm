package com.ipoint.coursegenerator.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.XmlObject;
import org.imsproject.xsd.imscpRootv1P1P2.ItemType;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestDocument;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;
import org.imsproject.xsd.imscpRootv1P1P2.OrganizationsType;
import org.imsproject.xsd.imscpRootv1P1P2.ResourceType;
import org.w3c.dom.Document;

import com.ipoint.coursegenerator.core.courseModel.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.CourseTreeNode;
import com.ipoint.coursegenerator.core.courseModel.PictureInfo;
import com.ipoint.coursegenerator.core.parsers.courseParser.CourseParser;
import com.ipoint.coursegenerator.core.utils.FileWork;
import com.ipoint.coursegenerator.core.utils.TransliterationTool;
import com.ipoint.coursegenerator.core.utils.Zipper;
import com.ipoint.coursegenerator.core.utils.manifest.ManifestProcessor;
import com.ipoint.coursegenerator.core.utils.manifest.MetadataProcessor;
import com.ipoint.coursegenerator.core.utils.manifest.OrganizationProcessor;
import com.ipoint.coursegenerator.core.utils.manifest.ResourcesProcessor;

public class Parser {

	private static final String PREFIX = "<manifest "
			+ "identifier=\"SingleSharableResource_MulitipleFileManifest\" version=\"1.1\" "
			+ "xmlns:ims=\"http://www.imsproject.org/xsd/imscp_rootv1p1p2\"><metadata/>";
	private static final String PREFIX_NEW = "<manifest xmlns=\"http://www.imsglobal"
			+ ".org/xsd/imscp_v1p1\" xmlns:imsmd=\"http://ltsc.ieee.org/xsd/LOM\" "
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:adlcp=\"http://www"
			+ ".adlnet.org/xsd/adlcp_v1p3\" xmlns:imsss=\"http://www.imsglobal.org/xsd/imsss\" "
			+ "xmlns:adlseq=\"http://www.adlnet.org/xsd/adlseq_v1p3\" xmlns:adlnav=\"http://www"
			+ ".adlnet.org/xsd/adlnav_v1p3\" " + "identifier=\"MANIFEST-5724A1B2-A6BE-F1BF-9781-706050DA4FC9\" "
			+ "xsi:schemaLocation=\"http://www.imsglobal.org/xsd/imscp_v1p1 imscp_v1p1.xsd "
			+ "http://ltsc.ieee.org/xsd/LOM lom.xsd http://www.adlnet.org/xsd/adlcp_v1p3 " + "adlcp_v1p3"
			+ ".xsd http://www.imsglobal.org/xsd/imsss imsss_v1p0.xsd http://www.adlnet"
			+ ".org/xsd/adlseq_v1p3 adlseq_v1p3.xsd http://www.adlnet.org/xsd/adlnav_v1p3 "
			+ "adlnav_v1p3.xsd\"><metadata><schema>ADL SCORM</schema><schemaversion>2004 4th "
			+ "Edition</schemaversion></metadata>";

	private static final String MANIFEST_NAME = "imsmanifest.xml";

	private File pathToSOffice = null;

	public Parser(String pathToSOffice) {
		File path = new File(pathToSOffice);
		if (path.exists()) {
			this.pathToSOffice = path;
		}
	}

	public Parser() {

	}

	private ManifestDocument createImsManifestFile(String courseName) {
		ManifestDocument manifest = ManifestDocument.Factory.newInstance();
		ManifestProcessor manifestProcessor = new ManifestProcessor();
		manifestProcessor.createManifest(manifest);

		// Add Metadata for Manifest
		MetadataProcessor metadataProcessor = new MetadataProcessor();
		metadataProcessor.createMetadata(manifest.getManifest());

		// Add Organization (default and root) to Manifest
		OrganizationProcessor.createOrganization(manifest.getManifest(), courseName);

		// Add Resources to Manifest
		ResourcesProcessor resourcesProcessor = new ResourcesProcessor();
		resourcesProcessor.createResources(manifest.getManifest());

		return manifest;
	}

	private String tuneManifest(ManifestDocument manifestDocument) {
		return manifestDocument.xmlText().replace("ims:", "").replace(PREFIX, PREFIX_NEW)
				.replace("datafromlms", "dataFromLMS").replace("adl:", "adlcp:").replace("rootv1p2", "v1p3")
				.replace(":adl=", ":adlcp=");
	}

	private static Document createNewHTMLDocument() {
		try {
			Document html = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			html.appendChild(html.createElement("html"));
			html.getFirstChild().appendChild(html.createElement("head"));
			html.getFirstChild().appendChild(html.createElement("body"));

			return html;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ItemType addOrganizationElementToManifest(XmlObject parentItem, CourseTreeNode node) {
		String id = UUID.randomUUID().toString();
		String itemId = "ITEM_" + id;
		String resourseId = (node.getPage() == null) ? null : "RES_" + id;

		ItemType manifestItem = null;
		if (parentItem instanceof ItemType) {
			manifestItem = OrganizationProcessor.createItem((ItemType) parentItem, node.getTitle(), resourseId, itemId);
		} else if (parentItem instanceof OrganizationsType) {
			manifestItem = OrganizationProcessor.createItem((OrganizationsType) parentItem, node.getTitle(), resourseId,
					itemId);
		}

		return manifestItem;
	}

	private void addScoToManifest(ManifestType manifest, ItemType manifestItem, File htmlFile,
			List<PictureInfo> images) {
		ResourceType itemResource = ResourcesProcessor.createScoResource(manifest, htmlFile,
				manifestItem.getIdentifierref());
		List<String> imagesNames = images.stream().map(image -> image.getName()).collect(Collectors.toList());

		ResourcesProcessor.addFilesToResource(imagesNames, itemResource);
	}

	private void addScoToCourse(CourseTreeNode node, File pageFile) {
		Document html = createNewHTMLDocument();
		html.getElementsByTagName("body").item(0).appendChild(node.getPage().toHtml(html));

		if (FileWork.saveHtmlDocument(html, pageFile)) {
			FileWork.saveImages(node.getPage().getImages(), new File(pageFile.getParentFile(), FileWork.IMAGE_DIR_NAME),
					pathToSOffice);
		}
	}

	private void saveCourse(List<CourseTreeNode> items, ManifestType manifest, File courseDir, XmlObject parentItem) {
		for (int i = 0; i < items.size(); i++) {
			CourseTreeNode item = items.get(i);

			ItemType manifestItem = addOrganizationElementToManifest(parentItem, item);
			if (item.getPage() != null) {
				File htmlFile = new File(courseDir, TransliterationTool.convertRU2ENString(item.getTitle())
						.replaceAll(" ", "_").replaceAll("[\\W&&[^-]]", "") + ".html");
				if (htmlFile.exists()) {
					htmlFile = new File(courseDir,
							TransliterationTool.convertRU2ENString(item.getTitle() + "_" + UUID.randomUUID().toString())
									.replaceAll(" ", "_").replaceAll("[\\W&&[^-]]", "") + ".html");
				}

				this.addScoToCourse(item, htmlFile);
				this.addScoToManifest(manifest, manifestItem, htmlFile, item.getPage().getImages());
			}

			if (!item.getNodes().isEmpty()) {
				this.saveCourse(item.getNodes(), manifest, courseDir, manifestItem);
			}
		}
	}

	// TODO: add variable for external templates
	public String parse(InputStream stream, int headerLevel, String courseName, String path) throws IOException {
		File directory = new File(path);
		if (directory.exists()) {
			FileUtils.deleteDirectory(directory);
		}
		directory.mkdirs();

		CourseModel courseModel = new CourseParser().parse(stream, courseName, headerLevel);

		ManifestDocument manifest = this.createImsManifestFile(courseName);

		if (!courseModel.getNodes().isEmpty()) {
			this.saveCourse(courseModel.getNodes(), manifest.getManifest(), directory,
					manifest.getManifest().getOrganizations());
		}

		String manifestContent = tuneManifest(manifest);
		FileWork.saveTextFile(manifestContent, new File(directory, MANIFEST_NAME));

		File jsDir = new File(directory, "js");
		File cssDir = new File(directory, "css");
		FileWork.copyFileFromResourcesToDir(new File("templates/html/kurs.css"), cssDir);
		FileWork.copyFileFromResourcesToDir(new File("templates/html/test.css"), cssDir);
		FileWork.copyFileFromResourcesToDir(new File("templates/js/APIWrapper.js"), jsDir);
		FileWork.copyFileFromResourcesToDir(new File("templates/js/SCOFunctions.js"), jsDir);
		FileWork.copyFileFromResourcesToDir(new File("templates/js/parser.js"), jsDir);

		String zipCourseFileName = getCourseZipFilename(courseName);
		Zipper zip = new Zipper(path + File.separator + zipCourseFileName, directory.getPath());
		zip.addToZip(new String[] { zipCourseFileName });

		return zipCourseFileName;
	}

	private String getCourseZipFilename(String courseName) {
		String zipCourseFileName = TransliterationTool.convertRU2ENString(courseName);
		Calendar date = GregorianCalendar.getInstance();
		zipCourseFileName = zipCourseFileName.replace(' ', '_').replaceAll("[\\W&&[^-]]", "");

		String sdate = "_" + Integer.toString(date.get(GregorianCalendar.YEAR)) + "-"
				+ Integer.toString(date.get(GregorianCalendar.MONTH) + 1) + "-"
				+ Integer.toString(date.get(GregorianCalendar.DATE));
		zipCourseFileName = zipCourseFileName + sdate + ".zip";
		return zipCourseFileName;
	}

}