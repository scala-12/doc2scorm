package com.ipoint.coursegenerator.core.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class FileUtils {

    public final static String HTML_PREFIX = "part_";

    public final static String IMAGE_PREFIX = "img_";

    public static void saveHTMLDocument(Document html, String path) {
	File file = new File(path);
	try {
	    StreamResult sr = new StreamResult(file.getAbsolutePath());
	    TransformerFactory.newInstance().newTransformer()
		    .transform(new DOMSource(html), sr);
	} catch (TransformerException e) {
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
	}
    }
}
