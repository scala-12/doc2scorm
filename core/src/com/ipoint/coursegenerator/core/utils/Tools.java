package com.ipoint.coursegenerator.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

public class Tools {

	public static byte[] convertStream2ByteArray(InputStream stream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n;
		while ((n = stream.read(buf)) >= 0) {
			baos.write(buf, 0, n);
		}

		return baos.toByteArray();
	}

	public static Document createNewHTMLDocument() {
		Document html = createEmptyDocument();
		html.appendChild(html.createElement("html"));
		html.getFirstChild().appendChild(html.createElement("head"));
		html.getFirstChild().appendChild(html.createElement("body"));

		return html;
	}

	public static Document createEmptyDocument() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}

}
