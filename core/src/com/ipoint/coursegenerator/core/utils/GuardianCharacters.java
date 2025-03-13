package com.ipoint.coursegenerator.core.utils;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class GuardianCharacters {

	private static Document htmlSVGDocument;

	static {
		fillCharactersData();
	}

	public GuardianCharacters() {

	}

	public static Document getCharactersData() {
		return htmlSVGDocument;
	}

	public static void fillCharactersData() {
		htmlSVGDocument = null;
		try {
			htmlSVGDocument = DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(GuardianCharacters.class.getClassLoader()
							.getResourceAsStream("opensymbol.svg"));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
