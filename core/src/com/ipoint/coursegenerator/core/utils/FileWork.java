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

	public final static String IMAGE_WMF = "image/x-wmf";
	public final static String IMAGE_PNG = "image/png";
	public final static String FILETYPE_DOCX = ".docx";
	public final static Charset STANDARD_ENCODING = StandardCharsets.UTF_8;
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
						try (OutputStreamWriter outStreamWriter = new OutputStreamWriter(fileOS, STANDARD_ENCODING)) {
							while ((bytesRead = is.read(buffer)) != -1) {
								outStreamWriter.write(new String(buffer, 0, bytesRead, STANDART_ENCODING));
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

	public static boolean saveTextFile(String content, File outFile) {
		return saveFile(new ByteArrayInputStream(content.getBytes(STANDART_ENCODING)), outFile, true);
	}

	public static boolean copyFileFromResourcesToDir(File fileFromResource, File destDir) {
		return saveFile(getFileFromResources(fileFromResource), new File(destDir, fileFromResource.getName()), false);
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
	 * @param pageFile
	 *            html file
	 * @return true if saved
	 */
	public static boolean saveHtmlDocument(Document htmlDoc, File pageFile) {
		try {
			StringWriter buffer = new StringWriter();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			NodeList bodyChilds = htmlDoc.getElementsByTagName("body").item(0).getChildNodes();
			for (int i = 0; i < bodyChilds.getLength(); i++) {
				transformer.transform(new DOMSource(bodyChilds.item(i)), new StreamResult(buffer));
			}
			Configuration cfg = new Configuration();
			cfg.setClassLoaderForTemplateLoading(FileWork.class.getClassLoader(), "templates/html");
			cfg.setObjectWrapper(new DefaultObjectWrapper());

			Template tmpl = cfg.getTemplate("index.ftl", STANDARD_ENCODING.name());
			Map<String, String> body = new HashMap<String, String>();
			body.put("bodycontent", buffer.toString());

			try (FileOutputStream htmlFOS = new FileOutputStream(pageFile);
					Writer outStreamWriter = new OutputStreamWriter(htmlFOS, STANDARD_ENCODING)) {
				tmpl.process(body, outStreamWriter);
				outStreamWriter.flush();

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

		return (byteImage == null) ? false
				: saveRawFile(new ByteArrayInputStream(byteImage), new File(imgsDir, pictureInfo.getName()));
	}

	/**
	 * Save images in directory
	 * 
	 * @param picturesInfo
	 *            List of picture info
	 * @param imgsDir
	 *            Absolute directory for saving
	 * @param sofficeFile
	 *            File for run LibreOffice
	 * @return true if have not errors
	 */
	public static boolean saveImages(List<PictureInfo> picturesInfo, File pageImgsDir, File pathToSOffice) {
		boolean successful = true;
		for (PictureInfo pictureInfo : picturesInfo) {
			successful = saveImage(pictureInfo, pageImgsDir, pathToSOffice) && successful;
		}

		return successful;
	}

}
