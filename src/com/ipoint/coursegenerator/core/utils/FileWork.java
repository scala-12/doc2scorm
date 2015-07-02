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

	public final static String IMAGE_PATH = "img".concat(File.separator);

	public final static String HTML_SUFFIX = ".html";

	public final static String IMAGE_WMF = "image/x-wmf";

	public final static String IMAGE_PNG = "image/png";

	public static boolean saveHTMLDocument(Document html, String templateDir,
			String coursePath, String pathInCourseToPage, String pageName) {
		boolean successful = false;
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
			for (String dirLevel : pathInCourseToPage.split((File.separator
					.equals("\\")) ? "\\\\" : File.separator)) {
				//TODO: if used java 8, then operator "if" (below)
				if (!dirLevel.isEmpty()) {
					fullPathToHtml = fullPathToHtml.concat(dirLevel).concat(
							File.separator);
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
					fullPathToHtml.concat(pageName)));
			temp.process(body, out);
			out.flush();
			successful = true;
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}

		return successful;
	}

	public static void saveImagesOfPage(List<ImageInfo> images, String pathToDir) {
		for (ImageInfo image : images) {
			String scrToImage = pathToDir.concat(image.getImageName()).replace(File.separatorChar, '/');
			byte[] picture = null;

			if (image.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG
					|| image.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG
					|| image.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP
					|| image.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF) {
				picture = image.getPictureData().getData();
			} else {
				if (!image.getPictureData().getPackagePart().getContentType()
						.equals("image/x-emf")
						|| image.getPictureData().getPackagePart()
								.getContentType().equals("image/emf")) {
					picture = ImageFormatConverter.transcodeWMFtoPNG(image
							.getPictureData().getData());
				} else {
					picture = ImageFormatConverter.transcodeEMFtoPNG(image
							.getPictureData().getData());
				}
			}

			savePNGImage(picture, scrToImage);
		}
	}

	private static void savePNGImage(byte[] picture, String path) {
		File file = new File(path);
		try {
			file.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(picture);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}
