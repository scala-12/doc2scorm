package com.ipoint.coursegenerator.core.elementparser;

import java.util.ArrayList;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.elementparser.graphics.AbstractGraphicsParser;
import com.ipoint.coursegenerator.core.elementparser.graphics.RasterGraphicsParser;
import com.ipoint.coursegenerator.core.elementparser.graphics.VectorGraphicsParser;

public class ParagraphParser extends AbstractElementParser {
    public static String parse(Object paragraph, Document html,
	    Object document, String path) {
	if (paragraph instanceof Paragraph && document != null) {
	    HWPFDocument doc = (HWPFDocument) document;
	    PicturesTable pictures = doc.getPicturesTable();
	    Paragraph par = (Paragraph) paragraph;
	    Element element = html.createElement("p");
	    ArrayList<Element> imgsToAppend = new ArrayList<Element>();
	    for (int i = 0; i < par.numCharacterRuns(); i++) {
		CharacterRun run = par.getCharacterRun(i);
		if (run.isSpecialCharacter()) {
		    if (pictures.hasPicture(run)) {
			Element imgElement = html.createElement("img");
			Picture picture = pictures.extractPicture(run, true);
			if (picture.getMimeType().equals(AbstractGraphicsParser.IMAGE_WMF)) {
			    VectorGraphicsParser.parse(picture, path, imgElement);
			} else {
			    RasterGraphicsParser.parse(picture, path, imgElement);
			}
			imgsToAppend.add(imgElement);
		    }
		} else if (!run.text().contains("EMBED Equation.3") && !run.text().equals(Character.toString((char)13))){
		    element.setTextContent(element.getTextContent()
			    + run.text());
		}
	    }
	    html.getElementsByTagName("body").item(0).appendChild(element);
	    for (Element el : imgsToAppend) {
		html.getElementsByTagName("body").item(0).appendChild(el);
	    }
	}
	return null;
    }
}
