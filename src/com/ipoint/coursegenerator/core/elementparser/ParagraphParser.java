package com.ipoint.coursegenerator.core.elementparser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.Bookmark;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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
		    	Element hyperlink = null;
		    	boolean isHyperlink = run.text().contains((char)19 + "");	//(char)19 is start of hyperlink
		    	if (isHyperlink) {	
		    	//search address of hyperlink
		    		hyperlink = html.createElement("a");
		    		run = par.getCharacterRun(++i);	//run in hyperlink. This run stored address
		    		String text = run.text().replace('\\', '/');
		    		int start = text.indexOf('"') + 1;	//start of address
    				int end = text.indexOf('"', start);	//end of address
    				
		    		String urlAddress = run.text().substring(start, end);	//address of hyperlink
		    		if (end != text.lastIndexOf('"')) {	//if address contains symbol ", then hyperlink has anchor
		    			urlAddress = urlAddress.concat("#").concat(text.substring(
		    					text.indexOf(
		    							'"', text.indexOf('"', end + 1)) + 1
		    							, text.lastIndexOf('"')));
		    		} else if (text.substring(0, start - 1).contains("/l")) {	//if hyperlink start with "/l" then this hyperlink is anchor
		    			urlAddress = "#".concat(urlAddress);
		    		}
		    		
		    		//TODO: add link on bookmark in text
		            if (urlAddress.toLowerCase().startsWith("http://") || urlAddress.toLowerCase().startsWith("https://")
							 || urlAddress.toLowerCase().startsWith("mailto:")
							 || urlAddress.toLowerCase().startsWith("ftp://")) {
		            	
						hyperlink.setAttribute("href", urlAddress);
						if (!urlAddress.toLowerCase().startsWith("mailto:")) {
							hyperlink.setAttribute("target", "_blank");
						}
					}
		    	}
		    	
		    	int endOfLink = -1;
		    	if (isHyperlink) {
		    		run = par.getCharacterRun(++i);	//into hyperlink tag
		    		for (int j = i+1; (endOfLink == -1) && (endOfLink < par.numCharacterRuns()); ++j) {
		    			if (par.getCharacterRun(j).text().contains((char)21 + "")) {	//(char)21 is end of hyperlink
		    				endOfLink = j;	//search end of hyperlink
		    			}
		    		}
		    	}
		    	
		    	for (int elem = i; (elem < par.numCharacterRuns()) && (elem != endOfLink); ++elem) {
		    		run = par.getCharacterRun(elem);
		    		if (run.isSpecialCharacter()) {	//run is something object MS Word
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
						    (isHyperlink ? hyperlink 
						    		: element).appendChild(imgElement);
						}
		    		} else if (isHyperlink) {
		    			Element inHyperlink = html.createElement("span");
						inHyperlink.setAttribute("class", "text_in_hyperlink");
						getTextFormatDOC(run, html, inHyperlink, hyperlink, par.getStyleIndex());
	    			} else {
	    				elem = par.numCharacterRuns();
		    		}		    		
		    	}
		    	
		    	if (isHyperlink) {
		    		element.appendChild(hyperlink);	//add hyperlink in html
		    		i = endOfLink;
		    	}
		    } else if (!run.text().equals(Character.toString((char) 13))) {
		    	Map<Integer, List<Bookmark>> bookmarks = doc.getBookmarks().getBookmarksStartedBetween(run.getStartOffset(), run.getEndOffset());
		    	if (!bookmarks.isEmpty()) {
	    			String textBefore = run.text();	//text before adding anchors
	    			int offset = 0;
	    			//adding anchors
	    			for (Iterator<List<Bookmark>>  iter = bookmarks.values().iterator(); iter.hasNext(); ) {
	    				Bookmark bookmark = iter.next().get(0);			//anchor
	    				int start = bookmark.getStart() - run.getStartOffset();			//start anchor in doc
	    				int end = ((start != bookmark.getEnd()) && ((bookmark.getEnd() - bookmark.getStart()) <= textBefore.length())) ?	//end anchor in text
								bookmark.getEnd() - run.getStartOffset()
								: textBefore.length();
						
	    				Element anchor = html.createElement("a");
	    				anchor.setAttribute("name", bookmark.getName());
	    				
	    				if (offset != start) {
	    					run.replaceText(textBefore.substring(offset, start), false);	//string before anchor
	    					getTextFormatDOC(run, html, element, parent, par.getStyleIndex());
	    					run.replaceText(textBefore, false);	//cancel changes
	    				}
	    				offset = end;
	    				
	    				run.replaceText(textBefore.substring(start, end), false);	//string with anchor
		    			getTextFormatDOC(run, html, anchor, element, par.getStyleIndex());
		    			run.replaceText(textBefore, false);	//cancel changes
	    	        }
	    			if (offset != textBefore.length()) {
	    				run.replaceText(textBefore.substring(offset, textBefore.length()), false);	//string before anchor
    					getTextFormatDOC(run, html, element, parent, par.getStyleIndex());
    					run.replaceText(textBefore, false);	//cancel changes
	    			}
		    	} else {
		    		getTextFormatDOC(run, html, element, parent, par.getStyleIndex());
		    	}
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
		Element element = createTextElement(styleIndex, html, headerInfo.getHeaderLevelNumber());
		ArrayList<Element> imagesElementsToAppend = new ArrayList<Element>();
		
		Element hyperlink = null;
		int end = -1;
		for (int i = 0; i < par.getRuns().size(); i++) {
		    XWPFRun run = par.getRuns().get(i);
		    
		    boolean isHyperlink = false;
		    if (run.getCTR().isSetRPr()) {
		    	Node node = run.getCTR().getDomNode();	//run as XML-tag <r>
		    	isHyperlink = node.getParentNode().getLocalName().equalsIgnoreCase("hyperlink");	//if run is hyperlink, then run is in tag <hyperlink>
		    	if (isHyperlink && (end == -1)) {	//if it is hyperlink and this run in the same tag <hyperlink>
		    		node = node.getParentNode();	//<hyperlink> as XML-tag
		    		hyperlink = html.createElement("a");
			    	end = i + (node.getChildNodes().getLength() - 1);	//number of last run in <hyperlink>
			    	NamedNodeMap linkParam = node.getAttributes();	//attributes of hyperlink
		    		if (linkParam.getNamedItem("r:id") != null) {	//if id is set then this hyperlink is external and address 
		    			String urlAddress = run.getDocument()
		    					.getHyperlinkByID(
		    							linkParam.getNamedItem("r:id").getNodeValue())
		    					.getURL().replace('\\', '/');
		    			if (urlAddress.toLowerCase().startsWith("http://") || urlAddress.toLowerCase().startsWith("https://")
		    					 || urlAddress.toLowerCase().startsWith("mailto:")
		    					 || urlAddress.toLowerCase().startsWith("ftp://")) {
		    			    if (linkParam.getNamedItem("w:anchor") != null) {	//if hyperlink has anchor
		    			    	urlAddress = urlAddress.concat("#").concat(linkParam.getNamedItem("w:anchor").getNodeValue());
		    			    }
		    				hyperlink.setAttribute("href", urlAddress);
		    				if (!urlAddress.toLowerCase().startsWith("mailto:")) {
								hyperlink.setAttribute("target", "_blank");	//open new window but not for sending mail
							}
		    			}
		    		} else {
		    			//TODO: create link on anchor in text
		    			
		    		}
		    	} else if ((node.getPreviousSibling() != null) && (end == -1)) {
    				if (node.getPreviousSibling().getLocalName().equals("bookmarkStart")) {	//if this run is bookmark then tag before <r> is <bookmarkStart/>
    					isHyperlink = true;
	    			} else if (node.getPreviousSibling().getLocalName().equals("bookmarkEnd")) {
	    				if (node.getPreviousSibling().getPreviousSibling().getLocalName().equals("bookmarkStart")) {
	    					isHyperlink = true;
	    					end = i;
	    				}
	    			}
    				if (isHyperlink) {
    					hyperlink = html.createElement("a");
	    				if (end != i) {
	    					hyperlink.setAttribute("name",  node.getPreviousSibling().getAttributes().getNamedItem("w:name").getNodeValue());
	    					end = i;
	    					for (Node iter = node.getNextSibling(); !iter.getLocalName().equals("bookmarkEnd"); iter = iter.getNextSibling()) {	//tag after last run in bookmark is <bookmarkEnd>	
	    						++end;	//search number of last run of bookmark
	    					}
	    				} else {
	    					hyperlink.setAttribute("name",  node.getPreviousSibling().getPreviousSibling().getAttributes().getNamedItem("w:name").getNodeValue());
	    				}
    				}
		    	}
		    }
		    
	    	if (run.toString().isEmpty()) {	//if run is not text
	    		if (run.getEmbeddedPictures() != null) {
	    			boolean picList = false;	//for choice method
	    			int endCycle = 0;
	    			if (run.getCTR().getPictList().isEmpty()) {
	    				//EmbeddedPictures 
	    				picList = false;
		    			endCycle = run.getEmbeddedPictures().size();
	    			} else {
	    				//PictList
	    				picList = true;
		    			endCycle = run.getCTR().getPictList().size();
	    			}
	    			
			    	for (int j = 0; j < endCycle; j++) {
					    Element imgElement = html.createElement("img");
					    XWPFPictureData pictureData = null;
					    if (picList) {
					    	CTPicture picture = run.getCTR().getPictList().get(j);
						    for (int k = 0; k < picture.getDomNode().getChildNodes().getLength(); ++k) {
						    	if (picture.getDomNode().getChildNodes().item(k).getLocalName().equalsIgnoreCase("shape")) {
						    		Node node = picture.getDomNode().getChildNodes().item(k).getFirstChild();
						    		for (; !node.getLocalName().equals("imagedata") && (node != null); node = node.getNextSibling()) {
						    		}
						    		if (node != null) {
						    			if (node.getLocalName().equals("imagedata")) {
						    				if (node.getAttributes().getNamedItem("r:id") != null) {
						    					pictureData = doc.getPictureDataByID(node.getAttributes().getNamedItem("r:id").getNodeValue());
						    				}
					    				}
					    			}
						    	}
						    }
					    } else {
					    	pictureData = run.getEmbeddedPictures().get(j).getPictureData();
					    }
					    
					    if (pictureData != null) {
						    if (pictureData.getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG
						    		|| pictureData.getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG
						    		|| pictureData.getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP
						    		|| pictureData.getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF) {
						    	RasterGraphicsParser.parse(pictureData, path, imgElement);
						    } else {
						    	VectorGraphicsParser.parse(pictureData, path, imgElement);
						    }
						    
						    imagesElementsToAppend.add(imgElement);
						    (isHyperlink ? hyperlink 
						    		: element).appendChild(imgElement);	//add object in hyperlink or html
					    }
					}
			    } else if (run.getCTR().sizeOfObjectArray() > 0) {
					for (CTObject picture : run.getCTR().getObjectArray()) {
						CTImageData[] objs = (CTImageData[]) picture
								.selectPath("declare namespace v='urn:schemas-microsoft-com:vml' " 
										+ ".//v:imagedata");
			
					    if (objs.length > 0) {
							Element imgElement = html.createElement("img");
							String rId = objs[0]
									.selectAttribute(
											"http://schemas.openxmlformats.org/officeDocument/2006/relationships",
											"id")
									.getDomNode().getNodeValue();
							XWPFPictureData pdata = null;
							for (XWPFPictureData pic : pictures) {
							    if (pic.getPackageRelationship().getId().equals(rId)) {
							    	pdata = pic;
							    	break;
							    }
							}
							if (pdata != null) {
							    if (pdata.getPackagePart().getContentType().equals("image/png")) {
							    	RasterGraphicsParser.parse(pdata, path, imgElement);
							    } else {
							    	VectorGraphicsParser.parse(pdata, path, imgElement);
							    }
							    imagesElementsToAppend.add(imgElement);
							    (isHyperlink ? hyperlink 
							    		: element).appendChild(imgElement);	//add object in hyperlink or html
							}
					    }
					}
			    }
	    	} else if (isHyperlink) {
		    	Element inHyperlink = html.createElement("span");
				inHyperlink.setAttribute("class", "text_in_hyperlink");
		    	getTextFormatDOCX(run, html, inHyperlink, hyperlink, styleIndex);	//add in <hyperlink> for html
		    } else {
		    	getTextFormatDOCX(run, html, element, parent, styleIndex);	//add element in html
		    }

		    if (i == end) {
	    		element.appendChild(hyperlink);	//add hyperlink in html
	    		end = -1;
		    }

		    parent.appendChild(element);
		    
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

    public static void getTextFormatDOCX(XWPFRun run,
    		Document creatorTags, Element el, Node parentElement, int styleNumber) {
    	
    	//TODO: search anchor in text
    	boolean isHyperlink = el.getNodeName().equalsIgnoreCase("a") || el.getAttribute("class").equalsIgnoreCase("text_in_hyperlink");
		el.removeAttribute("class");
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
		
		if (run.getSubscript() == VerticalAlign.SUPERSCRIPT) {
		    Element supElement = creatorTags.createElement("sup");
		    tempElement.appendChild(supElement);
		    tempElement = supElement;
		} else if (run.getSubscript() == VerticalAlign.SUBSCRIPT) {
		    Element subElement = creatorTags.createElement("sub");
		    tempElement.appendChild(subElement);
		    tempElement = subElement;
		}
		
		if (!isHyperlink) {
			if (run.getUnderline() != UnderlinePatterns.NONE) {
			    Element underlineText = creatorTags.createElement("u");
			    tempElement.appendChild(underlineText);
			    tempElement = underlineText;
			}
		}
		
		if ((styleNumber < 1) || (styleNumber > 9)) {
		    Element simpleText = creatorTags.createElement("font");
		    if ((run.getColor() != null) && !isHyperlink) {
		    	simpleText.setAttribute("color", run.getColor());
		    }
		    tempElement.appendChild(simpleText);
		    tempElement = simpleText;
		} else if ((styleNumber > 0) && (styleNumber < 10)) {
		    tempElement = el;
		}
		
		if (!tempElement.equals(el)) {
		    tempElement.setTextContent(run.toString());
		} else {
		    tempElement.setTextContent(tempElement.getTextContent() + run.toString());
		}
		
		parentElement.appendChild(el);
    }

    public static void getTextFormatDOC(CharacterRun run, Document creatorTags,
    		Element el, Node parentElement, int styleNumber) {	
		
    	boolean isHyperlink = el.getNodeName().equalsIgnoreCase("a") || el.getAttribute("class").equalsIgnoreCase("text_in_hyperlink");
		el.removeAttribute("class");
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
		
		if (run.getSubSuperScriptIndex() == 1) {	//is (subscript or superscript)
		    Element supElement = creatorTags.createElement("sup");
		    tempElement.appendChild(supElement);
		    tempElement = supElement;
		} else if (run.getSubSuperScriptIndex() == 2) {
		    Element subElement = creatorTags.createElement("sub");
		    tempElement.appendChild(subElement);
		    tempElement = subElement;
		}
		
		if (!isHyperlink) {
			if (run.getUnderlineCode() != 0) {	//is underlined
			    Element underlineText = creatorTags.createElement("u");
			    tempElement.appendChild(underlineText);
			    tempElement = underlineText;
			}
		}
		
		if ((styleNumber < 1) || (styleNumber > 9)) {
		    Element simpleText = creatorTags.createElement("font");
		    if ((run.getColor() != 0) && !isHyperlink) {
		    	simpleText.setAttribute("color", AbstractWordUtils.getColor(run.getColor()));
		    }
		    tempElement.appendChild(simpleText);
		    tempElement = simpleText;
		} else if ((styleNumber > 0) && (styleNumber < 10)) {
		    tempElement = el;
		}
		
		if (!tempElement.equals(el)) {
		    tempElement.setTextContent(run.text());
		} else {
		    tempElement.setTextContent(tempElement.getTextContent() + run.text());
		}
	
		parentElement.appendChild(el);
    }
}
