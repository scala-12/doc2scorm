package com.ipoint.coursegenerator.core.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
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

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FileWork {

	public final static String HTML_PREFIX = "part_";
	public final static String HTML_SUFFIX = ".html";
	public final static String IMAGE_PATH = "img".concat(File.separator);
	public final static String IMAGE_PREFIX = "img_";
	public final static String IMAGE_WMF = "image/x-wmf";
	public final static String IMAGE_PNG = "image/png";
	public final static String FILETYPE_DOCX = ".docx";

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
	public static boolean saveHTMLDocument(Document html, String templateDir,
			String coursePath, String pathInCourseToPage, String pageName) {
		try {
			StringWriter buffer = new StringWriter();
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			NodeList bodyChilds = html.getElementsByTagName("body").item(0)
					.getChildNodes();
			for (int i = 0; i < bodyChilds.getLength(); i++) {
				transformer.transform(new DOMSource(bodyChilds.item(i)),
						new StreamResult(buffer));
			}
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(templateDir));
			cfg.setObjectWrapper(new DefaultObjectWrapper());

			String upToLevel = new String();
			String fullPathToHtml = coursePath;
			if (!pathInCourseToPage.isEmpty()) {
				for (String dirLevel : pathInCourseToPage.split((File.separator
						.equals("\\")) ? "\\\\" : File.separator)) {
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
			Writer out = new OutputStreamWriter(new FileOutputStream(
					fullPathToHtml + pageName));
			temp.process(body, out);
			out.flush();
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
	public static boolean saveImage(ImageInfo image, String path) {
		String scrToImage = path.concat(image.getImageName()).replace(
				File.separatorChar, '/');
		byte[] byteImage = null;

		if (image.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG
				|| image.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG
				|| image.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP
				|| image.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF) {
			byteImage = image.getPictureData().getData();
		} else {
			if (!image.getPictureData().getPackagePart().getContentType()
					.equals("image/x-emf")
					|| image.getPictureData().getPackagePart().getContentType()
							.equals("image/emf")) {
				byteImage = ImageFormatConverter.transcodeWMFtoPNG(image
						.getPictureData().getData());
			} else {
				byteImage = ImageFormatConverter.transcodeEMFtoPNG(image
						.getPictureData().getData());
			}
		}

		return saveFile(byteImage, scrToImage);
	}

	/**
	 * Save image in directory
	 * 
	 * @param images
	 * @param path
	 * @return
	 */
	public static boolean saveImages(List<ImageInfo> images, String path) {
		boolean successful = true;
		for (ImageInfo image : images) {
			successful = successful && saveImage(image, path);
		}
		return successful;
	}

	/**
	 * Method for saving file in directory
	 * 
	 * @param byteFile
	 *            Byte file for adding
	 * @param path
	 *            Full path to creating file. Directory must be exist
	 * @return If added then true
	 */
	public static boolean saveFile(byte[] byteFile, String path) {
		File file = new File(path);
		try {
			file.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(byteFile);
			fos.close();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return false;
	}
}
