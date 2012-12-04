package com.ipoint.coursegenerator.core.elementparser;

import java.util.ArrayList;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
			VectorGraphicsParser.parse(
				pictures.extractPicture(run, true), path,
				imgElement);
			imgsToAppend.add(imgElement);
		    }
		    // FIXME: delete it
		    element.setTextContent(element.getTextContent()
			    + run.text());
		} else {
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
