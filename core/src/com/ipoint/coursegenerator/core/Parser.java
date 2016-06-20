package com.ipoint.coursegenerator.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
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

	private ManifestDocument manifest;

	private File pathToSOffice = null;

	public Parser(String pathToSOffice) {
		File path = new File(pathToSOffice);
		if (path.exists()) {
			this.pathToSOffice = path;
		}
	}

	public Parser() {

	}

	private void createImsManifestFile(String courseName) {
		manifest = ManifestDocument.Factory.newInstance();
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
	}

	private String tuneManifest(ManifestDocument manifestDocument) {
		String manifest = manifestDocument.xmlText();
		return manifest.replace("ims:", "").replace(PREFIX, PREFIX_NEW).replace("datafromlms", "dataFromLMS")
				.replace("adl:", "adlcp:").replace("rootv1p2", "v1p3").replace(":adl=", ":adlcp=");
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
		String id = java.util.UUID.randomUUID().toString();
		String itemId = "ITEM_".concat(id);
		String resourseId = (node.getPage() == null) ? null : "RES_".concat(id);

		ItemType manifestItem = null;
		if (parentItem instanceof ItemType) {
			manifestItem = OrganizationProcessor.createItem((ItemType) parentItem, node.getTitle(), resourseId, itemId);
		} else if (parentItem instanceof OrganizationsType) {
			manifestItem = OrganizationProcessor.createItem((OrganizationsType) parentItem, node.getTitle(), resourseId,
					itemId);
		}

		return manifestItem;
	}

	private void addImagesToManifest(ManifestType manifest, File relPageDir, List<PictureInfo> images,
			String resourseId) {
		ResourceType itemResource = ResourcesProcessor.createScoResource(manifest, relPageDir, resourseId);
		List<String> imagesNames = images.stream().map(image -> image.getName()).collect(Collectors.toList());

		ResourcesProcessor.addFilesToResource(getRelImgsDir(), imagesNames, itemResource);
	}

	private static File getRelImgsDir() {
		return new File(FileWork.IMAGE_DIR_NAME);
	}

	private static File getImgsDir(File courseDir) {
		return new File(courseDir, FileWork.IMAGE_DIR_NAME);
	}

	private void addPageToCourse(CourseTreeNode node, File courseDir, File relPageDir) {
		Document html = createNewHTMLDocument();
		html.getElementsByTagName("body").item(0).appendChild(node.getPage().toHtml(html));

		boolean pageAdded = FileWork.saveHtmlDocument(html, courseDir, relPageDir);
		if (pageAdded) {
			FileWork.saveImages(node.getPage().getImages(), getImgsDir(courseDir), pathToSOffice);
		}
	}

	private void saveCourse(List<CourseTreeNode> items, ManifestType manifest, File courseDir, XmlObject parentItem) {
		recursiveSaveCourse(items, manifest, courseDir, new File(""), null, parentItem);
	}

	private void recursiveSaveCourse(List<CourseTreeNode> items, ManifestType manifest, File courseDir, File relDir,
			String level, XmlObject parentItem) {
		String levelPrefix = (level == null) ? FileWork.HTML_PREFIX : (FileWork.HTML_PREFIX + level + "-");
		for (int i = 0; i < items.size(); i++) {
			CourseTreeNode item = items.get(i);

			String numberOnLevel = String.valueOf(i + 1);

			File relPageDir = new File(relDir,
					(levelPrefix + numberOnLevel + "_" + TransliterationTool.convertRU2ENString(item.getTitle()))
							.replaceAll(" ", "_").replaceAll("[\\W&&[^-]]", ""));

			ItemType manifestItem = addOrganizationElementToManifest(parentItem, item);
			if (item.getPage() != null) {
				this.addPageToCourse(item, courseDir, relPageDir);
				this.addImagesToManifest(manifest, relPageDir, item.getPage().getImages(),
						manifestItem.getIdentifierref());
			}

			if (!item.getNodes().isEmpty()) {
				this.recursiveSaveCourse(item.getNodes(), manifest, courseDir, relPageDir,
						(level == null) ? numberOnLevel : (level + "-" + numberOnLevel), manifestItem);
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

		this.createImsManifestFile(courseName);

		if (!courseModel.getNodes().isEmpty()) {
			this.saveCourse(courseModel.getNodes(), manifest.getManifest(), directory,
					manifest.getManifest().getOrganizations());
		}

		String manifestContent = tuneManifest(manifest);
		File manifestFile = new File(path, "imsmanifest.xml");

		FileWork.saveTextFile(new ByteArrayInputStream(manifestContent.getBytes()), manifestFile);

		File jsDir = new File(directory, "js");
		File cssDir = new File(directory, "css");
		FileWork.copyRawFileFromResourcesToDir(new File("templates/html/kurs.css"), cssDir);
		FileWork.copyRawFileFromResourcesToDir(new File("templates/html/test.css"), cssDir);
		FileWork.copyRawFileFromResourcesToDir(new File("templates/html/APIWrapper.js"), jsDir);
		FileWork.copyRawFileFromResourcesToDir(new File("templates/js/SCOFunctions.js"), jsDir);
		FileWork.copyRawFileFromResourcesToDir(new File("templates/js/parser.js"), jsDir);

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