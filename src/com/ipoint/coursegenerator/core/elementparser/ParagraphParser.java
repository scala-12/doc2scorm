package com.ipoint.coursegenerator.core.elementparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
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
		    element.appendChild(imgElement);
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
		getTextFormatDOC(run, html, element, parent,
			par.getStyleIndex());
		// element.setTextContent(element.getTextContent() +
		// run.text());
	    }
	}
	headerText = element.getTextContent();
	parent.appendChild(element);
	for (Element el : imgsToAppend) {
	    // parent.appendChild(el);
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
	for (int i = 0; i < par.getRuns().size(); i++) {
	    XWPFRun run = par.getRuns().get(i);
	    getTextFormatDOCX(run, parent, html, styleIndex, element);
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
		    element.appendChild(imageElement);
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
			    element.appendChild(imageElement);
			}
		    }
		}
	    }
	    headerText = element.getTextContent();
	}
	for (Element el : imagesElementsToAppend) {
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

    public static void getTextFormatDOCX(XWPFRun run, Node parentElement,
	    Document creatorTags, int styleNumber, Element el) {
	Element tempElement = el;

	if (run.isBold()) {
	    Element boldText = creatorTags.createElement("b");
	    tempElement.appendChild(boldText);
	    tempElement = boldText;
	}
	if (run.isItalic()) {
	    Element italicText = creatorTags.createElement("i");
	    tempElement.appendChild(italicText);
	    tempElement = italicText;
	}
	if (run.getUnderline() != UnderlinePatterns.NONE) {
	    Element underlineText = creatorTags.createElement("u");
	    tempElement.appendChild(underlineText);
	    tempElement = underlineText;
	}
	if (run.getSubscript() == VerticalAlign.SUPERSCRIPT) {
	    Element supElement = creatorTags.createElement("sup");
	    tempElement.appendChild(supElement);
	    tempElement = supElement;
	} else if (run.getSubscript() == VerticalAlign.SUBSCRIPT) {
	    Element subElement = creatorTags.createElement("sub");
	    tempElement.appendChild(subElement);
	    tempElement = subElement;
	}
	if (styleNumber > 9 || styleNumber < 1) {
	    Element simpleText = creatorTags.createElement("font");
	    if (run.getColor() != null) {
		simpleText.setAttribute("color", run.getColor());
	    }
	    tempElement.appendChild(simpleText);
	    tempElement = simpleText;

	} else if (styleNumber > 0 && styleNumber < 10) {
	    tempElement = el;
	}
	if (!tempElement.equals(el)) {
	    tempElement.setTextContent(run.toString());
	} else {
	    tempElement.setTextContent(tempElement.getTextContent()
		    + run.toString());
	}
	parentElement.appendChild(el);

	// new code
	/*
	 * if(run.getParagraph().getCTP().getHyperlinkList().size()>0) { Element
	 * hyperLinkElement = creatorTags.createElement("a");
	 * hyperLinkElement.setAttribute("href", "http://google.ru");
	 * tempElement.appendChild(hyperLinkElement); tempElement =
	 * hyperLinkElement; }
	 */
    }

    public static void getTextFormatDOC(CharacterRun run, Document creatorTags,
	    Element el, Node parentElement, int styleNumber) {
	Element tempElement = el;
	if (run.isBold()) {
	    Element boldText = creatorTags.createElement("b");
	    tempElement.appendChild(boldText);
	    tempElement = boldText;
	}
	if (run.isItalic()) {
	    Element italicText = creatorTags.createElement("i");
	    tempElement.appendChild(italicText);
	    tempElement = italicText;
	}
	if (run.getUnderlineCode() != 0) {
	    Element underlineText = creatorTags.createElement("u");
	    tempElement.appendChild(underlineText);
	    tempElement = underlineText;
	}
	if (run.getSubSuperScriptIndex() == 1) {
	    Element supElement = creatorTags.createElement("sup");
	    tempElement.appendChild(supElement);
	    tempElement = supElement;
	} else if (run.getSubSuperScriptIndex() == 2) {
	    Element subElement = creatorTags.createElement("sub");
	    tempElement.appendChild(subElement);
	    tempElement = subElement;
	}
	if (styleNumber > 9 || styleNumber < 1) {
	    Element simpleText = creatorTags.createElement("font");
	    simpleText.setTextContent(run.text());
	    if (run.getColor() != 0) {
		simpleText.setAttribute("color",
			AbstractWordUtils.getColor(run.getColor()));
	    }
	    tempElement.appendChild(simpleText);
	    tempElement = simpleText;
	} else if (styleNumber > 0 && styleNumber < 10) {
	    tempElement = el;
	}
	if (!tempElement.equals(el)) {
	    tempElement.setTextContent(run.text());
	} else {
	    tempElement.setTextContent(tempElement.getTextContent()
		    + run.text());
	}

	parentElement.appendChild(el);
    }
}
