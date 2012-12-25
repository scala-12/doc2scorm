package com.ipoint.coursegenerator.core.elementparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import schemasMicrosoftComVml.CTImageData;

import com.ipoint.coursegenerator.core.Parser;
import com.ipoint.coursegenerator.core.elementparser.graphics.AbstractGraphicsParser;
import com.ipoint.coursegenerator.core.elementparser.graphics.RasterGraphicsParser;
import com.ipoint.coursegenerator.core.elementparser.graphics.VectorGraphicsParser;

public class ParagraphParser extends AbstractElementParser {

    public static String parse(Object paragraph, Document html,
	    Object document, String path, HeaderInfo headerInfo, Node parent) {
	if (paragraph instanceof Paragraph) {
	    return parse((Paragraph) paragraph, html, (HWPFDocument) document,
		    path, headerInfo, parent);
	} else if (paragraph instanceof XWPFParagraph) {
	    return parse((XWPFParagraph) paragraph, html,
		    (XWPFDocument) document, path, headerInfo, parent);
	}
	return null;
    }

    public static String parse(Paragraph par, Document html, HWPFDocument doc,
	    String path, HeaderInfo headerInfo, Node parent) {
	String headerText = "";
	PicturesTable pictures = doc.getPicturesTable();

	Element element = createTextElement(par.getStyleIndex(), html,
		headerInfo.getHeaderLevelNumber());
	ArrayList<Element> imgsToAppend = new ArrayList<Element>();

	for (int i = 0; i < par.numCharacterRuns(); i++) {
	    CharacterRun run = par.getCharacterRun(i);
	    if (run.isSpecialCharacter()) {
		if (pictures.hasPicture(run)) {
		    Element imgElement = html.createElement("img");
		    Picture picture = pictures.extractPicture(run, true);
		    if (picture.getMimeType().equals(
			    AbstractGraphicsParser.IMAGE_WMF)) {
			VectorGraphicsParser.parse(picture, path, imgElement);
		    } else {
			RasterGraphicsParser.parse(picture, path, imgElement);
		    }
		    imgsToAppend.add(imgElement);
		}
		int offset = i;
		for (offset = i + 1; offset < par.numCharacterRuns(); offset++) {
		    if (par.getCharacterRun(offset).text()
			    .contains((char) 20 + "")) {
			i = offset;
			break;
		    }
		}
	    } else if (!run.text().equals(Character.toString((char) 13))) {
		element.setTextContent(element.getTextContent() + run.text());
	    }
	}
	headerText = element.getTextContent();
	parent.appendChild(element);
	for (Element el : imgsToAppend) {
	    parent.appendChild(el);
	    headerInfo.addResourceFile(el.getAttribute("src"));
	}
	return headerText;

    }

    public static String parse(XWPFParagraph paragraph, Document html,
	    XWPFDocument doc, String path, HeaderInfo headerInfo, Node parent) {
	String headerText = "";
	final List<XWPFPictureData> pictures = doc.getAllPackagePictures();
	XWPFParagraph par = (XWPFParagraph) paragraph;
	int styleIndex = 0;
	styleIndex = Parser.getCurrentParagraphStyleID(par);
	Element element = createTextElement(styleIndex, html,
		headerInfo.getHeaderLevelNumber());
	ArrayList<Element> imagesElementsToAppend = new ArrayList<Element>();
	// element.setTextContent(par.getText());

	/*
	 * for(int i = 0; i < par.getRuns().size(); i++) { XWPFRun paragraphRun
	 * = par.getRuns().get(i); if (paragraphRun.getColor() != null) {
	 * element.setAttribute("color", paragraphRun.getColor()); } }
	 */
	for (int i = 0; i < par.getRuns().size(); i++) {
	    XWPFRun paragraphRun = par.getRuns().get(i);
	    getTextFormat(paragraphRun, parent, html, styleIndex, element);
	    /*
	     * if (paragraphRun.isBold()) { Element boldText =
	     * html.createElement("b");
	     * boldText.setTextContent(paragraphRun.toString()); if
	     * (paragraphRun.getColor() != null) { Element parentFontElement =
	     * html.createElement("font");
	     * parentFontElement.setAttribute("color", paragraphRun.getColor());
	     * parentFontElement.appendChild(boldText);
	     * parent.appendChild(parentFontElement); } else {
	     * parent.appendChild(boldText); } } else if
	     * (paragraphRun.isItalic()) { Element italicText =
	     * html.createElement("i");
	     * italicText.setTextContent(paragraphRun.toString()); if
	     * (paragraphRun.getColor() != null) {
	     * italicText.setAttribute("color", paragraphRun.getColor()); }
	     * parent.appendChild(italicText);
	     * 
	     * } else { if (styleIndex > 9) { Element simpleText =
	     * html.createElement("span");
	     * simpleText.setTextContent(paragraphRun.toString());
	     * 
	     * element.setTextContent(paragraphRun.toString()); if
	     * (paragraphRun.getColor() != null) {
	     * simpleText.setAttribute("color", paragraphRun.getColor()); }
	     * parent.appendChild(simpleText); } else if (styleIndex > 0 &&
	     * styleIndex < 10) {
	     * element.setTextContent(paragraphRun.toString());
	     * parent.appendChild(element); } }
	     */
	}

	// parent.appendChild(element);
	for (int i = 0; i < par.getRuns().size(); i++) {
	    XWPFRun run = par.getRuns().get(i);
	    if (run.getEmbeddedPictures() != null) {
		for (int j = 0; j < run.getEmbeddedPictures().size(); j++) {
		    Element imageElement = html.createElement("img");
		    XWPFPicture picture = run.getEmbeddedPictures().get(j);
		    if (picture.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG
			    || picture.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG
			    || picture.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP
			    || picture.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF) {
			RasterGraphicsParser.parse(picture.getPictureData(),
				path, imageElement);
		    } else {
			VectorGraphicsParser.parse(picture.getPictureData(),
				path, imageElement);
		    }
		    imagesElementsToAppend.add(imageElement);
		}
	    }
	    if (run.getCTR().sizeOfObjectArray() > 0) {
		for (CTObject picture : run.getCTR().getObjectArray()) {
		    CTImageData[] objs = (CTImageData[]) picture
			    .selectPath("declare namespace v='urn:schemas-microsoft-com:vml' "
				    + ".//v:imagedata");

		    if (objs.length > 0) {
			Element imageElement = html.createElement("img");
			String rId = objs[0]
				.selectAttribute(
					"http://schemas.openxmlformats.org/officeDocument/2006/relationships",
					"id").getDomNode().getNodeValue();
			XWPFPictureData pdata = null;
			for (XWPFPictureData pic : pictures) {
			    if (pic.getPackageRelationship().getId()
				    .equals(rId)) {
				pdata = pic;
				break;
			    }
			}
			if (pdata != null) {
			    if (pdata.getPackagePart().getContentType()
				    .equals("image/png")) {
				RasterGraphicsParser.parse(pdata, path,
					imageElement);
			    } else {
				VectorGraphicsParser.parse(pdata, path,
					imageElement);
			    }
			    imagesElementsToAppend.add(imageElement);
			}
		    }
		}
	    }
	    headerText = element.getTextContent();

	}
	for (Element el : imagesElementsToAppend) {
	    parent.appendChild(el);
	    headerInfo.addResourceFile(el.getAttribute("src"));
	}
	return headerText;
    }

    public static Element createTextElement(int styleIndex, Document html,
	    int headerLevel) {
	Element element = null;
	if (styleIndex > 0 && styleIndex <= headerLevel) {
	    element = html.createElement("h1");
	} else if (styleIndex > headerLevel && styleIndex < 10
		&& (styleIndex - headerLevel + 1) < 7) {
	    element = html.createElement("h" + (styleIndex - headerLevel + 1));
	} else if (styleIndex > headerLevel && styleIndex < 10
		&& (styleIndex - headerLevel + 1) > 6) {
	    element = html.createElement("h6");
	} else {
	    element = html.createElement("p");
	}
	return element;
    }

    public static boolean isNumericParagraphStyle(String stringStyleID) {
	if (stringStyleID != null) {
	    Pattern pattern = Pattern.compile("[0-9]*");
	    Matcher matcher = pattern.matcher(stringStyleID);
	    return matcher.matches();
	}
	return false;
    }

    public void parseParagraphRuns() {

    }

    public static void getTextFormat(XWPFRun run, Node parentElement,
	    Document creatorTags, int styleNumber, Element el) {
	if (run.isBold()) {
	    Element boldText = creatorTags.createElement("b");
	    boldText.setTextContent(run.toString());
	    if (run.getColor() != null) {
		Element parentFontElement = creatorTags.createElement("font");
		parentFontElement.setAttribute("color", run.getColor());
		parentFontElement.appendChild(boldText);
		el.appendChild(parentFontElement);
		parentElement.appendChild(el);
	    } else {
		el.appendChild(boldText);
		parentElement.appendChild(el);
	    }
	} else if (run.isItalic()) {
	    Element italicText = creatorTags.createElement("i");
	    italicText.setTextContent(run.toString());
	    if (run.getColor() != null) {
		Element parentFontElement = creatorTags.createElement("font");
		parentFontElement.setAttribute("color", run.getColor());
		parentFontElement.appendChild(italicText);
		el.appendChild(parentFontElement);
		parentElement.appendChild(el);
	    } else {
		el.appendChild(italicText);
		parentElement.appendChild(el);
	    }
	} else if (run.getUnderline() != UnderlinePatterns.NONE) {
	    Element underlineText = creatorTags.createElement("u");
	    underlineText.setTextContent(run.toString());
	    if (run.getColor() != null) {
		Element parentFontElement = creatorTags.createElement("font");
		parentFontElement.setAttribute("color", run.getColor());
		parentFontElement.appendChild(underlineText);
		el.appendChild(parentFontElement);
		parentElement.appendChild(el);
	    } else {
		el.appendChild(underlineText);
		parentElement.appendChild(el);
	    }
	} else {
	    if (styleNumber > 9) {
		// Element simpleText = creatorTags.createElement("span");
		Element simpleText = creatorTags.createElement("font");
		simpleText.setTextContent(run.toString());

		// el.setTextContent(run.toString());
		if (run.getColor() != null) {
		    simpleText.setAttribute("color", run.getColor());
		    el.appendChild(simpleText);
		    parentElement.appendChild(el);
		} else {
		    el.appendChild(simpleText);
		    parentElement.appendChild(el);
		}
	    } else if (styleNumber > 0 && styleNumber < 10) {
		el.setTextContent(run.toString());
		parentElement.appendChild(el);
	    }
	    parentElement.appendChild(el);
	}

    }
}
