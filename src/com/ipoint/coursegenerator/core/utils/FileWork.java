package com.ipoint.coursegenerator.core.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.PictureInfo;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FileWork {

	public final static String HTML_PREFIX = "part_";
	public final static String HTML_SUFFIX = ".html";
	public final static String IMAGE_WMF = "image/x-wmf";
	public final static String IMAGE_PNG = "image/png";
	public final static String FILETYPE_DOCX = ".docx";
	public final static Charset STANDART_ENCODING = StandardCharsets.UTF_8;
	public final static String IMAGE_DIR_NAME = "img";

	private static boolean saveFile(InputStream is, File outFile, boolean isText) {
		if (null == is) {
			return false;
		} else {
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}

			try {
				outFile.createNewFile();
				try (FileOutputStream fileOS = new FileOutputStream(outFile)) {
					byte[] buffer = new byte[1024];
					int bytesRead;
					if (isText) {
						try (OutputStreamWriter outStreamWriter = new OutputStreamWriter(fileOS, STANDART_ENCODING)) {
							while ((bytesRead = is.read(buffer)) != -1) {
								outStreamWriter.write(new String(buffer, 0, bytesRead));
							}
						}
					} else {
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

	public static boolean saveRawFile(InputStream is, File outFile) {
		return saveFile(is, outFile, false);
	}

	public static boolean saveTextFile(InputStream is, File outFile) {
		return saveFile(is, outFile, true);
	}

	public static boolean copyTextFileFromResourcesToDir(File destDir, String fromDir, String textFileName) {
		return copyFileFromResourcesToDir(destDir, fromDir, textFileName, true);
	}

	public static boolean copyRawFileFromResourcesToDir(File destDir, String fromDir, String rawFileName) {
		return copyFileFromResourcesToDir(destDir, fromDir, rawFileName, false);
	}

	private static boolean copyFileFromResourcesToDir(File destDir, String fromDir, String fileName, boolean isText) {
		return saveFile(getFileFromResources(fromDir, fileName), new File(destDir, fileName), isText);
	}

	public static InputStream getFileFromResources(String fromDir, String fileName) {
		return FileWork.class.getClassLoader().getResourceAsStream(fromDir + "/" + fileName);
	}

	/**
	 * Save html-page in directory
	 *
	 * @param html
	 *            Saving html-page
	 * @param templateDir
	 *            Directory with template of course
	 * @param coursePath
	 *            Path to course
	 * @param pathInCourseToPage
	 *            Path in course to page
	 * @param pageName
	 *            Name of adding html-page
	 * @return
	 */
	public static boolean saveHTMLDocument(Document html, String coursePath, String pathInCourseToPage,
			String pageName) {
		try {
			StringWriter buffer = new StringWriter();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			NodeList bodyChilds = html.getElementsByTagName("body").item(0).getChildNodes();
			for (int i = 0; i < bodyChilds.getLength(); i++) {
				transformer.transform(new DOMSource(bodyChilds.item(i)), new StreamResult(buffer));
			}
			Configuration cfg = new Configuration();
			cfg.setClassLoaderForTemplateLoading(FileWork.class.getClassLoader(), "templates/html");
			cfg.setObjectWrapper(new DefaultObjectWrapper());

			String upToLevel = new String();
			String fullPathToHtml = coursePath;
			if (!pathInCourseToPage.isEmpty()) {
				for (String dirLevel : pathInCourseToPage
						.split((File.separator.equals("\\")) ? "\\\\" : File.separator)) {
					fullPathToHtml = fullPathToHtml + dirLevel + File.separator;
					File f = new File(fullPathToHtml);
					if (!f.exists()) {
						f.mkdirs();
					}
					upToLevel += "../";
				}
			}

			fullPathToHtml = fullPathToHtml.replace(File.separatorChar, '/');

			Template temp = cfg.getTemplate("index.ftl", "UTF-8");
			Map<String, String> body = new HashMap<String, String>();
			body.put("bodycontent", buffer.toString());
			body.put("upToLevel", upToLevel);
			Writer outStreamWriter = new OutputStreamWriter(new FileOutputStream(fullPathToHtml + pageName),
					STANDART_ENCODING);
			temp.process(body, outStreamWriter);
			outStreamWriter.flush();
			outStreamWriter.close();

			return true;
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
	 * @param images
	 *            Image for adding
	 * @param path
	 *            Path to directory in which saving image
	 * @return If added then true
	 */
	public static boolean saveImage(PictureInfo image, String path, File pathToSOffice) {
		String scrToImage = path.concat(image.getName()).replace(File.separatorChar, '/');
		byte[] byteImage = null;

		if (image.getData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG
				|| image.getData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG
				|| image.getData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP
				|| image.getData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF) {
			byteImage = image.getData().getData();
		} else {
			if (!image.getData().getPackagePart().getContentType().equals("image/x-emf")
					|| image.getData().getPackagePart().getContentType().equals("image/emf")) {
				byteImage = ImageFormatConverter.transcodeWmfToPng(image.getData().getData(), pathToSOffice);
			} else {
				byteImage = ImageFormatConverter.transcodeEmfToPng(image.getData().getData(), pathToSOffice);
			}
		}

		// TODO: remove this statement. It's used because we have problems with
		// wmf/emf
		return (byteImage == null) ? false : saveRawFile(new ByteArrayInputStream(byteImage), new File(scrToImage));
	}

	/**
	 * Save image in directory
	 *
	 * @param images
	 * @param path
	 * @return
	 */
	public static boolean saveImages(List<PictureInfo> images, String path, File pathToSOffice) {
		boolean successful = true;
		for (PictureInfo image : images) {
			successful = saveImage(image, path, pathToSOffice) && successful;
		}
		return successful;
	}

}
