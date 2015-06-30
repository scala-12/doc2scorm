package com.ipoint.coursegenerator.core.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FileWork {

	public final static String HTML_PREFIX = "part_";

	public final static String IMAGE_PREFIX = "img_";
	
	public final static String HTML_SUFFIX = ".html";

	public static void saveHTMLDocument(Document html, String templateDir,
			String htmlPath, String coursePath) {
		try {
			// StreamResult sr = new StreamResult(file.getAbsolutePath());
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
			int level = 0;
			if (htmlPath.contains(coursePath)) {
				level = StringUtils.countMatches(
						htmlPath.substring(htmlPath.length()
								- htmlPath.compareTo(coursePath)),
						File.separator);
			}
			level = level - 2;
			String upToLevel = "";
			for (int i = 0; i < level; i++) {
				upToLevel += "../";
			}
			Template temp = cfg.getTemplate("index.ftl", "UTF-8");
			Map<String, String> body = new HashMap<String, String>();
			body.put("bodycontent", buffer.toString());
			body.put("upToLevel", upToLevel);
			Writer out = new OutputStreamWriter(new FileOutputStream(htmlPath));
			temp.process(body, out);
			out.flush();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	public static void savePNGImage(byte[] picture, String path) {
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
