package com.ipoint.coursegenerator.core.elementparser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.elementparser.graphics.AbstractGraphicsParser;
import com.ipoint.coursegenerator.core.elementparser.graphics.RasterGraphicsParser;
import com.ipoint.coursegenerator.core.elementparser.graphics.VectorGraphicsParser;

public class ParagraphParser extends AbstractElementParser {

    public static String parse(Object paragraph, Document html,
	    Object document, String path, int headerLevel) {
	String headerText = "";
	if (paragraph instanceof Paragraph && document != null) {
	    HWPFDocument doc = (HWPFDocument) document;
	    PicturesTable pictures = doc.getPicturesTable();
	    Paragraph par = (Paragraph) paragraph;
	    Element element = createTextElement(par.getStyleIndex(), html,
		    headerLevel);
	    ArrayList<Element> imgsToAppend = new ArrayList<Element>();
	    for (int i = 0; i < par.numCharacterRuns(); i++) {
		CharacterRun run = par.getCharacterRun(i);
		if (run.isSpecialCharacter()) {
		    if (pictures.hasPicture(run)) {
			Element imgElement = html.createElement("img");
			Picture picture = pictures.extractPicture(run, true);
			if (picture.getMimeType().equals(
				AbstractGraphicsParser.IMAGE_WMF)) {
			    VectorGraphicsParser.parse(picture, path,
				    imgElement);
			} else {
			    RasterGraphicsParser.parse(picture, path,
				    imgElement);
			}
			imgsToAppend.add(imgElement);
		    }
		} else if (!run.text().contains("EMBED Equation.3")
			&& !run.text().equals(Character.toString((char) 13))) {
		    element.setTextContent(element.getTextContent()
			    + run.text());
		}
	    }
	    headerText = element.getTextContent();
	    html.getElementsByTagName("body").item(0).appendChild(element);
	    for (Element el : imgsToAppend) {
		html.getElementsByTagName("body").item(0).appendChild(el);
	    }
	} else if (paragraph instanceof XWPFParagraph && document != null) {
	    XWPFDocument doc = (XWPFDocument) document;
	    final List<XWPFPictureData> pictures = doc.getAllPackagePictures();
	    final Map<BigInteger, Element> listMap = new HashMap<BigInteger, Element>();
	    XWPFParagraph par = (XWPFParagraph) paragraph;
	    int styleIndex = 0;
	    if (isNumericParagraphStyle(par.getStyleID())) {
		styleIndex = Integer.parseInt(par.getStyleID());
	    } else {
		styleIndex = (int) 100;
	    }
	    Element element = createTextElement(styleIndex, html, headerLevel);
	    ArrayList<Element> imagesElementsToAppend = new ArrayList<Element>();
	    element.setTextContent(par.getText());
	    html.getElementsByTagName("body").item(0).appendChild(element);
	    for (int i = 0; i < par.getRuns().size(); i++) {

		XWPFRun run = par.getRuns().get(i);
		if (run.getEmbeddedPictures() != null) {
		    for (int j = 0; j < run.getEmbeddedPictures().size(); j++) {
			Element imageElement = html.createElement("img");
			XWPFPicture picture = run.getEmbeddedPictures().get(j);
			if (picture.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG
				|| picture.getPictureData().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG) {
			    RasterGraphicsParser.parse(picture, path,
				    imageElement);
			} else {

			    VectorGraphicsParser.parse(picture, path,
				    imageElement);
			}
			imagesElementsToAppend.add(imageElement);

		    }

		}
		if (run.getCTR().sizeOfObjectArray() > 0) {
		    for (CTObject picture : run.getCTR().getObjectArray()) {
			XmlObject[] objs = picture
				.selectPath("declare namespace v='urn:schemas-microsoft-com:vml' "
					+ ".//v:imagedata");

			if (objs.length > 0) {
			    Element imageElement = html.createElement("img");
			    String rId = ((XmlAnyTypeImpl) ((XmlAnyTypeImpl) objs[0])
				    .selectAttribute(
					    "http://schemas.openxmlformats.org/officeDocument/2006/relationships",
					    "id")).getStringValue();
			    XWPFPictureData pdata = null;
			    for (XWPFPictureData pic : pictures) {
				if (pic.getPackageRelationship().getId()
					.equals(rId)) {
				    pdata = pic;
				    break;
				}
			    }
			    if (pdata != null) {

				VectorGraphicsParser.parse(pdata, path,
					imageElement);
				imagesElementsToAppend.add(imageElement);
			    }
			}

		    }
		}

		// doc.getNumbering().getNum(par.getNumID()).getCTNum().getAbstractNumId().getVal();
		for (POIXMLDocumentPart p : doc.getRelations()) {
		    String relation = p.getPackageRelationship()
			    .getRelationshipType();
		    if (relation.equals(XWPFRelation.NUMBERING.getRelation())) {
			XWPFNumbering numbering = (XWPFNumbering) p;
			if (par.getNumID() != null) {
			    Element listElement = html.createElement("li");
			    listMap.put(
				    doc.getNumbering().getNum(par.getNumID())
					    .getCTNum().getAbstractNumId()
					    .getVal(), listElement);
			    CTNum num = numbering.getNum(par.getNumID())
				    .getCTNum();
			    num.getNumId();
			    System.out.println(numbering.getNum(par.getNumID())
				    .getCTNum().getAbstractNumId().getVal());
			    numbering.getAbstractNum(
				    numbering.getNum(par.getNumID()).getCTNum()
					    .getAbstractNumId().getVal())
				    .getCTAbstractNum();
			    System.out.println(num);
			}

		    }
		}
		headerText = element.getTextContent();
		for (Element el : imagesElementsToAppend) {
		    html.getElementsByTagName("body").item(0).appendChild(el);
		}

	    }

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
}
