package com.ipoint.coursegenerator.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.XmlObject;
import org.imsproject.xsd.imscpRootv1P1P2.ItemType;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestDocument;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;
import org.imsproject.xsd.imscpRootv1P1P2.OrganizationsType;
import org.imsproject.xsd.imscpRootv1P1P2.ResourceType;
import org.w3c.dom.Document;

import com.ipoint.coursegenerator.core.courseModel.content.PictureInfo;
import com.ipoint.coursegenerator.core.courseModel.content.TestingPage;
import com.ipoint.coursegenerator.core.courseModel.content.TheoryPage;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionBlock;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseTreeNode;
import com.ipoint.coursegenerator.core.parsers.courseParser.CourseParser;
import com.ipoint.coursegenerator.core.utils.FileWork;
import com.ipoint.coursegenerator.core.utils.Tools;
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

	/** From iLogos */
	public static final String COURSE_SYSTEM_DIR = "system_files";

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
			Set<PictureInfo> images) {
		ResourceType itemResource = ResourcesProcessor.createScoResource(manifest, htmlFile,
				manifestItem.getIdentifierref());
		List<String> imagesNames = images.stream().map(image -> image.getName()).collect(Collectors.toList());

		ResourcesProcessor.addFilesToResource(imagesNames, itemResource);
	}

	private void saveScoInCourse(ManifestType manifest, ItemType manifestItem, CourseTreeNode node, File courseDir) {
		String scoName = TransliterationTool.convertRU2ENString(node.getTitle());
		if (node.getPage() instanceof TheoryPage) {
			File file = new File(courseDir, scoName.replaceAll(" ", "_").replaceAll("[\\W&&[^-]]", "") + ".html");
			if (file.exists()) {
				file = new File(courseDir, (scoName + "_" + UUID.randomUUID().toString()).replaceAll(" ", "_")
						.replaceAll("[\\W&&[^-]]", "") + ".html");
			}

			Document html = Tools.createNewHTMLDocument();
			html.getElementsByTagName("body").item(0).appendChild(node.getPage().toHtml(html));

			if (FileWork.saveTheoryHtmlDocument(html, file, node.getTitle())) {
				Set<PictureInfo> pics = ((TheoryPage) node.getPage()).getImages();
				FileWork.saveImages(pics, new File(file.getParentFile(), FileWork.IMAGE_DIR_NAME), pathToSOffice);
				this.addScoToManifest(manifest, manifestItem, file, pics);
			}
		} else if (node.getPage() instanceof TestingPage) {
			File dir = new File(courseDir, scoName.replaceAll(" ", "_").replaceAll("[\\W&&[^-]]", ""));
			if (dir.exists()) {
				dir = new File(courseDir, (scoName + "_" + UUID.randomUUID().toString()).replaceAll(" ", "_")
						.replaceAll("[\\W&&[^-]]", ""));
			}

			TestingPage page = (TestingPage) node.getPage();
			StringBuilder intro = new StringBuilder();

			for (AbstractParagraphBlock<?> block : page.getIntroBlocks()) {
				intro.append(block.toHtml(Tools.createNewHTMLDocument()));
			}
			FileWork.saveTestingDir(dir, intro.toString());

			for (int i = 0; i < page.getBlocks().size(); i++) {
				AbstractQuestionBlock<?> block = page.getBlocks().get(i);
				Document html = Tools.createNewHTMLDocument();
				html.getElementsByTagName("body").item(0).appendChild(block.toHtml(html));
				File file = new File(dir, String.valueOf(i + 1) + ".html");
				if (FileWork.saveTheoryHtmlDocument(html, file, node.getTitle())) {
					Set<PictureInfo> pics = block.getImages();
					FileWork.saveImages(pics, new File(dir, FileWork.IMAGE_DIR_NAME), pathToSOffice);
					this.addScoToManifest(manifest, manifestItem, file, pics);
				}
			}
		}
	}

	private void saveCourse(List<CourseTreeNode> items, ManifestType manifest, File courseDir, XmlObject parentItem) {
		for (int i = 0; i < items.size(); i++) {
			CourseTreeNode item = items.get(i);

			ItemType manifestItem = addOrganizationElementToManifest(parentItem, item);
			if (item.getPage() != null) {
				this.saveScoInCourse(manifest, manifestItem, item, courseDir);
			}

			if (!item.getChilds().isEmpty()) {
				this.saveCourse(item.getChilds(), manifest, courseDir, manifestItem);
			}
		}
	}

	// TODO: add variable for external templates
	public String parse(InputStream stream, int headerLevel, String courseName, String path) throws IOException {
		courseName = courseName.trim().replaceAll("\\s\\s+", " ");

		File directory = new File(path);
		if (directory.exists()) {
			FileUtils.deleteDirectory(directory);
		}
		directory.mkdirs();

		CourseModel courseModel = CourseParser.parse(stream, courseName, headerLevel);

		ManifestDocument manifest = this.createImsManifestFile(courseName);

		if (!courseModel.getChilds().isEmpty()) {
			this.saveCourse(courseModel.getChilds(), manifest.getManifest(), directory,
					manifest.getManifest().getOrganizations());
		}

		String manifestContent = tuneManifest(manifest);
		FileWork.saveTextFile(manifestContent, new File(directory, MANIFEST_NAME));

		FileWork.saveSystemDir(new File(directory, COURSE_SYSTEM_DIR));

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