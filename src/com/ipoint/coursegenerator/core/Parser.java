package com.ipoint.coursegenerator.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestDocument;

import com.google.gson.GsonBuilder;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.ModelTreeNode;
import com.ipoint.coursegenerator.core.parsers.courseParser.CourseParser;
import com.ipoint.coursegenerator.core.utils.FileTools;
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

	private final Optional<File> sOfficeFile;

	/** From iLogos */
	public static final String COURSE_SYSTEM_DIR = "system_files";

	public Parser(Optional<String> pathToSOffice) {
		File sOfficeFile = null;
		if (pathToSOffice.isPresent()) {
			File path = new File(pathToSOffice.get());
			if (path.exists()) {
				sOfficeFile = path;
			}
		}

		this.sOfficeFile = (sOfficeFile == null) ? Optional.empty() : Optional.of(sOfficeFile);
	}

	public Parser() {
		this(Optional.empty());
	}

	private String tuneManifest(ManifestDocument manifestDocument) {
		return manifestDocument.xmlText().replace("ims:", "").replace(PREFIX, PREFIX_NEW)
				.replace("datafromlms", "dataFromLMS").replace("adl:", "adlcp:").replace("rootv1p2", "v1p3")
				.replace(":adl=", ":adlcp=");
	}

	// TODO: add variable for external templates
	public String parse(InputStream stream, int headerLevel, String courseName, String path) throws IOException {
		courseName = Tools.removeExtraSpaces(courseName);

		File courseDir = new File(path);
		if (courseDir.exists()) {
			FileUtils.deleteDirectory(courseDir);
		}
		courseDir.mkdirs();

		CourseModel courseModel = CourseParser.parse(stream, courseName, headerLevel);
		saveImsManifestFile(courseModel, courseDir);

		FileTools.saveSystemDir(new File(courseDir, COURSE_SYSTEM_DIR), courseModel.hasFormulas());

		String zipCourseFileName = getCourseZipFilename(courseName);
		Zipper zip = new Zipper(path + File.separator + zipCourseFileName, courseDir.getPath());
		zip.addToZip(new String[] { zipCourseFileName });

		return zipCourseFileName;
	}

	/**
	 * @return JSON of map(map(node-id, title, type) to parent-node-id).<br>
	 *         Type may be from enum: root, theory, test, header
	 */
	public String getJsonCourseModel(InputStream stream, int headerLevel) throws IOException {
		return new GsonBuilder().create()
				.toJson(CourseParser.parseHeadersOnly(stream, "Empty model", headerLevel).getHierarchyInfo());
	}

	private void saveImsManifestFile(CourseModel courseModel, File courseDir) throws IOException {
		ManifestDocument manDocument = ManifestDocument.Factory.newInstance();

		// Create Manifest for Manifest Document
		ManifestProcessor manProcessor = new ManifestProcessor();
		manProcessor.createManifest(manDocument);

		// Add Metadata for Manifest
		MetadataProcessor metadataProcessor = new MetadataProcessor();
		metadataProcessor.createMetadata(manDocument.getManifest());

		if (!courseModel.getChilds().isEmpty()) {
			String courseSysName = Tools.generateSystemName(courseModel.getTitle());
			OrganizationProcessor organizationProcessor = new OrganizationProcessor(manDocument.getManifest(),
					courseModel.getTitle(), courseSysName);
			ResourcesProcessor resourcesProcessor = new ResourcesProcessor(manDocument.getManifest());
			createManifestScoUnitAndSavePage(courseDir, courseModel.getChilds(), organizationProcessor,
					resourcesProcessor);
		}

		String manContent = tuneManifest(manDocument);
		File manFile = new File(courseDir, "imsmanifest.xml");
		manFile.createNewFile();

		FileTools.saveTextFile(manContent, manFile);
	}

	private void createManifestScoUnitAndSavePage(File courseDir, List<ModelTreeNode> nodes,
			OrganizationProcessor organizationProcessor, ResourcesProcessor resourcesProcessor) {
		nodes.stream().forEach(node -> {
			organizationProcessor.createItem(node);
			resourcesProcessor.createItem(node);
			FileTools.saveCoursePageAsHtmlDocument(node.getPage(), courseDir, this.sOfficeFile);

			if (!node.getChilds().isEmpty()) {
				this.createManifestScoUnitAndSavePage(courseDir, node.getChilds(), organizationProcessor,
						resourcesProcessor);
			}
		});
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