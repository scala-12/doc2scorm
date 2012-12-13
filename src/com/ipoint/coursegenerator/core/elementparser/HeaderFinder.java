package com.ipoint.coursegenerator.core.elementparser;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.hwpf.usermodel.Paragraph;
import org.imsproject.xsd.imscpRootv1P1P2.ItemType;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;
import org.w3c.dom.Document;

import com.ipoint.coursegenerator.core.utils.FileWork;
import com.ipoint.coursegenerator.core.utils.TransliterationTool;
import com.ipoint.coursegenerator.core.utils.manifest.OrganizationProcessor;
import com.ipoint.coursegenerator.core.utils.manifest.ResourcesProcessor;

public class HeaderFinder {

    private static Document createNewHTMLDocument() {
	try {
	    Document html = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder().newDocument();
	    html.appendChild(html.createElement("html"));
	    html.getFirstChild().appendChild(html.createElement("head"));
	    html.getFirstChild().appendChild(html.createElement("body"));
	    return html;
	} catch (ParserConfigurationException e) {

	}
	return null;
    }

    public static Document parse(Object paragraph, Document html,
	    HeaderInfo headerInfo, ArrayList<ItemInfo> items, Object document,
	    ManifestType manifest, int parStyleId) {
	ItemInfo itemInfo = null;
	ItemInfo lastItem = items.get(items.size() - 1);
	if (html == null) {
	    html = createNewHTMLDocument();
	}
	boolean createItemForHeader = parStyleId <= headerInfo
		.getHeaderLevelNumber() && parStyleId > 0;
	if (createItemForHeader && !headerInfo.isFirstHeader()
		&& headerInfo.getPreviousParStyleID() != parStyleId) {
	    if (html != null) {
		FileWork.saveHTMLDocument(
			html,
			headerInfo.getTemplateDir(),
			lastItem.getHtmlPath() + File.separator
				+ lastItem.getFilename(), lastItem.getPath());
	    }
	    html = createNewHTMLDocument();
	}
	String paragraphText = ParagraphParser.parse(paragraph, html, document,
		lastItem.getHtmlPath(), headerInfo.getHeaderLevelNumber(), html
			.getElementsByTagName("body").item(0));
	if (createItemForHeader) {
	    if (headerInfo.getPreviousParStyleID() == parStyleId) {
		headerInfo.setHeaderText(headerInfo.getHeaderText()
			+ paragraphText);
	    } else {
		headerInfo.setHeaderText(paragraphText);
	    }
	    headerInfo.setHeaderStyleID(parStyleId);
	}
	if (createItemForHeader && headerInfo.getNextParStyleID() != parStyleId) {
	    headerInfo.setFirstHeader(false);
	    headerInfo
		    .setHeaderText(headerInfo.getHeaderText().length() > 127 ? headerInfo
			    .getHeaderText().substring(0, 127) : headerInfo
			    .getHeaderText());
	    itemInfo = createItem(items, headerInfo.getHeaderText(),
		    headerInfo.getHeaderStyleID(), manifest);
	    itemInfo.setHtmlPath(lastItem.getPath() + File.separator
		    + itemInfo.getUrl());
	    itemInfo.setPath(lastItem.getPath());
	    items.add(itemInfo);
	    File f = new File(itemInfo.getHtmlPath());
	    if (!f.exists()) {
		f.mkdirs();
	    }
	}
	headerInfo.setPreviousParStyleID(parStyleId);
	return html;
    }

    public static ItemInfo createItem(ArrayList<ItemInfo> items,
	    String scoName, int styleIndex, ManifestType manifest) {
	String path = "";
	ItemType parentItem = null;
	String itemText = TransliterationTool.convertRU2ENString(scoName);
	itemText = itemText.replaceAll(" ", "_");
	itemText = itemText.replaceAll("[\\W&&[^-]]", "");
	for (int j = items.size() - 1; j >= 0; j--) {
	    if (items.get(j).getStyleId() < styleIndex) {
		parentItem = items.get(j).getItem();
		path = items.get(j).getDirectoryPath();
		if (j == (items.size() - 1)) {
		    String resid = parentItem.getIdentifierref();
		    parentItem.getDomNode().getAttributes()
			    .removeNamedItem("identifierref");

		    OrganizationProcessor.createItem(parentItem,
			    parentItem.getTitle(), resid, "ITEM_"
				    + java.util.UUID.randomUUID().toString());
		}
		break;
	    }
	}
	String itemid = "ITEM_" + java.util.UUID.randomUUID().toString();
	String resid = "RES_" + java.util.UUID.randomUUID().toString();
	ItemType item = null;
	if (parentItem == null) {
	    item = OrganizationProcessor.createItem(
		    manifest.getOrganizations(), scoName, resid, itemid);
	} else {
	    item = OrganizationProcessor.createItem(parentItem, scoName, resid,
		    itemid);
	}
	String filename = FileWork.HTML_PREFIX + Integer.toString(items.size())
		+ "_" + itemText + ".htm";
	ResourcesProcessor.createResource(manifest, path + filename, resid);
	ItemInfo itemInfo = new ItemInfo(item, styleIndex, path
		+ FileWork.HTML_PREFIX + Integer.toString(items.size()) + "_"
		+ itemText + '/', path, filename);
	return itemInfo;
    }
}
