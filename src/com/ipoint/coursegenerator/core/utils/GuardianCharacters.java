package com.ipoint.coursegenerator.core.utils;

import java.io.File;
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
	try {
	    htmlSVGDocument = DocumentBuilderFactory
		    .newInstance()
		    .newDocumentBuilder()
		    .parse(new File(
			    "C:\\workspaces\\ilogos\\ilogos-course-generator-core\\opensymbol.svg"));
	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}
    }
}
