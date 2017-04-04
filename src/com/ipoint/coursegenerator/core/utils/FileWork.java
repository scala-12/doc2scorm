
package com.ipoint.coursegenerator.core.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.Parser;
import com.ipoint.coursegenerator.core.courseModel.content.PictureInfo;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FileWork {

	public final static String IMAGE_WMF = "image/x-wmf";
	public final static String IMAGE_PNG = "image/png";
	public final static String FILETYPE_DOCX = ".docx";
	public final static Charset STANDARD_ENCODING = StandardCharsets.UTF_8;
	public final static String IMAGE_DIR_NAME = "img";

	public static class TemplateFiles {
		private static final String TEMPLATE_DIR = "templates";
		public static final File JS_DIR = new File(TEMPLATE_DIR, "js");
		public static final File CSS_DIR = new File(TEMPLATE_DIR, "css");

		public final static File SCO4THEORY = new File(TEMPLATE_DIR, "sco_theory_template.ftl");
		public final static File SCO4TEST = new File(TEMPLATE_DIR, "sco_test_template.ftl");

		public final static File CSS4THEORY = new File(CSS_DIR, "theory.css");
		public final static File CSS4TEST = new File(CSS_DIR, "test.css");
		public final static File CSS4COURSE = new File(CSS_DIR, "course.css");

		public static final String JQUERY_VERSION = "3.1.1";
		public static final String JQUERY_UI_VERSION = "1.12.1";
	}

	private static boolean saveFile(InputStream is, File outFile, boolean isText) {
		if (null != is) {
			if (outFile.getParentFile().exists() || outFile.getParentFile().mkdirs()) {
				try {
					outFile.createNewFile();
					try (FileOutputStream fileOS = new FileOutputStream(outFile)) {
						int bytesRead;
						if (isText) {
							char[] buffer = new char[1024];
							try (OutputStreamWriter writerOS = new OutputStreamWriter(fileOS, STANDARD_ENCODING);
									InputStreamReader readerIS = new InputStreamReader(is, STANDARD_ENCODING)) {
								while ((bytesRead = readerIS.read(buffer)) != -1) {
									writerOS.write(buffer, 0, bytesRead);
								}
							}
						} else {
							byte[] buffer = new byte[1024];
							while ((bytesRead = is.read(buffer)) != -1) {
								fileOS.write(buffer, 0, bytesRead);
							}
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				return true;
			}
		}

		return false;
	}

	public static boolean saveRawFile(InputStream is, File outFile) {
		return saveFile(is, outFile, false);
	}

	public static boolean saveTextFile(String content, File outFile) {
		return saveFile(new ByteArrayInputStream(content.getBytes(STANDARD_ENCODING)), outFile, true);
	}

	public static boolean copyFileFromResourcesToDir(File fileFromResource, File destDir) {
		return saveFile(getFileFromResources(fileFromResource), new File(destDir, fileFromResource.getName()), false);
	}

	public static void copyFileFromResourceDirToDir(File resourceDir, File destDir) {
		String resDirPath = resourceDir.getPath().replace(File.separatorChar, '/');
		
		String jarPath = FileWork.class.getResource("/" + resDirPath).getFile();
		if (jarPath.startsWith("file:")) {
			jarPath = jarPath.substring("file:/".length());
		}
		if (jarPath.contains("!")) {
			jarPath = jarPath.substring(0, jarPath.indexOf('!'));
		}

		try (JarFile jar = new JarFile(new File(jarPath));) {
			final Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {
					String fileName = entry.getName().replace(File.separatorChar, '/');
					if ((fileName.length() > resDirPath.length()) && fileName.startsWith(resDirPath + '/')) {
						saveFile(getFileFromResources(new File(resourceDir, fileName)), new File(destDir, fileName),
								false);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static InputStream getFileFromResources(File fileFromResource) {
		String path = fileFromResource.getPath().replace(File.separatorChar, '/');

		return FileWork.class.getClassLoader().getResourceAsStream(
				(path.startsWith(File.separator)) ? path.substring(File.separator.length()) : path);
	}

	/**
	 * Save html page
	 *
	 * @param htmlDoc
	 *            Html document
	 * @param htmlFile
	 *            html file
	 * @return true if saved
	 */
	public static boolean saveHtmlDocument(Document htmlDoc, File htmlFile, String pageTitle) {
		try {
			StringWriter buffer = new StringWriter();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			NodeList bodyChilds = htmlDoc.getElementsByTagName("body").item(0).getChildNodes();
			for (int i = 0; i < bodyChilds.getLength(); i++) {
				transformer.transform(new DOMSource(bodyChilds.item(i)), new StreamResult(buffer));
			}
			Configuration cfg = new Configuration();
			cfg.setClassLoaderForTemplateLoading(FileWork.class.getClassLoader(), TemplateFiles.SCO4THEORY.getParent());
			cfg.setObjectWrapper(new DefaultObjectWrapper());

			Template tmpl = cfg.getTemplate(TemplateFiles.SCO4THEORY.getName(), STANDARD_ENCODING.name());
			Map<String, String> body = new HashMap<>();

			body.put("page_title", pageTitle);
			body.put("system_dir", Parser.COURSE_SYSTEM_DIR);
			body.put("theory_css", TemplateFiles.CSS4THEORY.getName());
			body.put("course_css", TemplateFiles.CSS4COURSE.getName());
			body.put("jquery_ver", TemplateFiles.JQUERY_VERSION);
			body.put("jquery_ui_ver", TemplateFiles.JQUERY_UI_VERSION);

			body.put("body_content", buffer.toString());

			try (FileOutputStream htmlFOS = new FileOutputStream(htmlFile);
					Writer writerOS = new OutputStreamWriter(htmlFOS, STANDARD_ENCODING)) {
				tmpl.process(body, writerOS);
				writerOS.flush();

				return true;
			}
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Save image in directory
	 *
	 * @param pictureInfo
	 *            Picture info
	 * @param imgsDir
	 *            Absolute directory for saving
	 * @param sofficeFile
	 *            File for run LibreOffice
	 * @return true if have not errors
	 */
	public static boolean saveImage(PictureInfo pictureInfo, File imgsDir, File sofficeFile) {
		byte[] byteImage = null;

		switch (pictureInfo.getData().getPictureType()) {
		case org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG:
		case org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP:
		case org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF:
		case org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG:
			byteImage = pictureInfo.getData().getData();
			break;
		case org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_EMF:
			byteImage = ImageFormatConverter.transcodeEmfToPng(pictureInfo.getData().getData(), sofficeFile);
			break;
		case org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_WMF:
			byteImage = ImageFormatConverter.transcodeWmfToPng(pictureInfo.getData().getData(), sofficeFile);
			break;
		}

		return (byteImage != null)
				&& saveRawFile(new ByteArrayInputStream(byteImage), new File(imgsDir, pictureInfo.getName()));
	}

	/**
	 * Save images in directory
	 *
	 * @param picturesInfo
	 *            List of picture info
	 * @param pageImgsDir
	 *            Absolute directory for saving
	 * @param pathToSOffice
	 *            File for run LibreOffice
	 * @return true if have not errors
	 */
	public static boolean saveImages(Set<PictureInfo> picturesInfo, File pageImgsDir, File pathToSOffice) {
		boolean successful = true;
		for (PictureInfo pictureInfo : picturesInfo) {
			successful = saveImage(pictureInfo, pageImgsDir, pathToSOffice) && successful;
		}

		return successful;
	}

}
