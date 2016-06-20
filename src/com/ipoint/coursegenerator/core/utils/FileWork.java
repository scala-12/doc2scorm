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

	public static boolean copyTextFileFromResourcesToDir(File textFileFromResource, File destDir) {
		return copyFileFromResourcesToDir(textFileFromResource, destDir, true);
	}

	public static boolean copyRawFileFromResourcesToDir(File rawFileFromResource, File destDir) {
		return copyFileFromResourcesToDir(rawFileFromResource, destDir, false);
	}

	private static boolean copyFileFromResourcesToDir(File fileFromResource, File destDir, boolean isText) {
		return saveFile(getFileFromResources(fileFromResource), new File(destDir, fileFromResource.getName()), isText);
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
	 * @param courseDir
	 *            Absolute course directory
	 * @param relPageDir
	 *            html file which relative to course directory
	 * @return true if saved
	 */
	public static boolean saveHtmlDocument(Document htmlDoc, File courseDir, File relPageDir) {
		try {
			String htmlPath = new File(relPageDir.getPath() + FileWork.HTML_SUFFIX).getPath();

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

			int level = (htmlPath.startsWith(File.separator) ? htmlPath.substring(File.separator.length()) : htmlPath)
					.split((File.separator.equals("\\") ? "\\\\" : File.separator)).length - 1;

			StringBuilder upToLevel = new StringBuilder();
			for (int i = 0; i < level; i++) {
				upToLevel.append("../");
			}

			File absoluteHtmlFile = new File(courseDir, htmlPath);
			absoluteHtmlFile.getParentFile().mkdirs();

			Template temp = cfg.getTemplate("index.ftl", "UTF-8");
			Map<String, String> body = new HashMap<String, String>();
			body.put("bodycontent", buffer.toString());
			body.put("upToLevel", upToLevel.toString());

			try (FileOutputStream htmlFOS = new FileOutputStream(absoluteHtmlFile);
					Writer outStreamWriter = new OutputStreamWriter(htmlFOS, STANDART_ENCODING)) {
				temp.process(body, outStreamWriter);
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

		if (pictureInfo.getData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG
				|| pictureInfo.getData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG
				|| pictureInfo.getData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP
				|| pictureInfo.getData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF) {
			byteImage = pictureInfo.getData().getData();
		} else {
			if (!pictureInfo.getData().getPackagePart().getContentType().equals("image/x-emf")
					|| pictureInfo.getData().getPackagePart().getContentType().equals("image/emf")) {
				byteImage = ImageFormatConverter.transcodeWmfToPng(pictureInfo.getData().getData(), sofficeFile);
			} else {
				byteImage = ImageFormatConverter.transcodeEmfToPng(pictureInfo.getData().getData(), sofficeFile);
			}
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
