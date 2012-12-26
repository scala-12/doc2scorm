package com.ipoint.coursegenerator.core.elementparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.HWPFList;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ListParser {
    private LinkedHashMap<Integer, HashMap<Integer, ArrayList<Element>>> listsIds = null;
    private int previousParIlvl = 0;

    public ListParser() {
	reset();
    }

    public void setPreviousParIlvl(int previousParIlvl) {
	this.previousParIlvl = previousParIlvl;
    }

    public void reset() {
	previousParIlvl = 0;
	listsIds = new LinkedHashMap<Integer, HashMap<Integer, ArrayList<Element>>>();
    }

    public Element createHTMLList(int numberFormat, int startAt, Document html) {
	Element htmlList;
	switch (numberFormat) {
	case 0:
	    // Numeric (arabic) list
	    htmlList = html.createElement("ol");
	    break;
	case 4:
	    // small latin letters list
	    htmlList = html.createElement("ol");
	    htmlList.setAttribute("type", "a");
	    break;
	case 58:
	    // small cyrillic letters list
	    htmlList = html.createElement("ol");
	    htmlList.setAttribute("type", "а");
	    break;
	case 23:
	    // bullet list
	    htmlList = html.createElement("ul");
	    break;
	default:
	    htmlList = html.createElement("ol");
	    break;
	}
	htmlList.setAttribute("start", String.valueOf(startAt));
	return htmlList;
    }

    public void parse(Paragraph par, Document html, HWPFDocument document,
	    HeaderInfo headerInfo, String path) {
	HWPFList list = par.getList();
	if (listsIds.values().size() > 0) {
	    HashMap<Integer, ArrayList<Element>> map = (HashMap<Integer, ArrayList<Element>>) listsIds
		    .values().toArray()[0];
	    if (listsIds.values().size() > 0) {
		ArrayList<Element> arr = (ArrayList<Element>) map.values()
			.toArray()[0];
		if (arr.size() > 0) {
		    if (!arr.get(0).getOwnerDocument().equals(html)) {
			this.reset();
		    }
		}
	    }
	}
	int lastListId = getLastListId();
	Element li = html.createElement("li");
	ParagraphParser.parse(par, html, document, path, headerInfo, li);
	if (listsIds.get(list.getLsid()) == null
		|| lastListId != list.getLsid() || previousParIlvl < 0) {
	    Node parent = html.getElementsByTagName("body").item(0);
	    HashMap<Integer, ArrayList<Element>> subListsMap = listsIds
		    .get(list.getLsid()) != null ? listsIds.get(list.getLsid())
		    : new HashMap<Integer, ArrayList<Element>>();
	    for (int i = 0; i <= par.getIlvl(); i++) {

		int startat = list.getListData().getLevel(i + 1).getStartAt();
		ArrayList<Element> subLists = new ArrayList<Element>();
		if ((lastListId != list.getLsid() || previousParIlvl < 0)
			&& listsIds.get(list.getLsid()) != null
			&& listsIds.get(list.getLsid()).get(i) != null) {
		    Element ol = listsIds
			    .get(list.getLsid())
			    .get(i)
			    .get(listsIds.get(list.getLsid()).get(i).size() - 1);
		    if (ol.getAttribute("start") != null) {
			startat = Integer.parseInt(ol.getAttribute("start"))
				+ ol.getChildNodes().getLength();
		    }
		    subLists = listsIds.get(list.getLsid()).get(i);
		}
		Element htmlList = this.createHTMLList(
			list.getNumberFormat((char) i), startat, html);

		subLists.add(htmlList);
		subListsMap.put(i, subLists);
		parent.appendChild(htmlList);
		parent = htmlList;
	    }
	    listsIds.remove(list.getLsid());
	    listsIds.put(list.getLsid(), subListsMap);
	}

	if (listsIds.get(list.getLsid()) != null) {
	    ArrayList<Element> subLists = listsIds.get(list.getLsid()).get(
		    par.getIlvl());
	    if (subLists != null
		    && (previousParIlvl >= par.getIlvl() || previousParIlvl < 0)) {
		subLists.get(subLists.size() - 1).appendChild(li);
	    } else if (previousParIlvl < par.getIlvl()) {
		int i = 100;
		for (i = par.getIlvl() - 1; i > -1; i--) {
		    if (listsIds.get(list.getLsid()).get(i) != null) {
			Element htmlList = this.createHTMLList(
				list.getNumberFormat((char) par.getIlvl()),
				list.getListData().getLevel(par.getIlvl() + 1)
					.getStartAt(), html);
			subLists = new ArrayList<Element>();
			subLists.add(htmlList);
			listsIds.get(list.getLsid()).put(par.getIlvl(),
				subLists);
			htmlList.appendChild(li);
			subLists = listsIds.get(list.getLsid()).get(i);
			subLists.get(subLists.size() - 1).appendChild(htmlList);
			break;
		    }
		}
	    }
	}
	previousParIlvl = par.getIlvl();
    }

    public void parse(XWPFParagraph par, Document html, XWPFDocument document,
	    HeaderInfo headerInfo, String path) {
	// HWPFList list = par.getList();
	if (listsIds.values().size() > 0) {
	    HashMap<Integer, ArrayList<Element>> map = (HashMap<Integer, ArrayList<Element>>) listsIds
		    .values().toArray()[0];
	    if (listsIds.values().size() > 0) {
		ArrayList<Element> arr = (ArrayList<Element>) map.values()
			.toArray()[0];
		if (arr.size() > 0) {
		    if (!arr.get(0).getOwnerDocument().equals(html)) {
			this.reset();
		    }
		}
	    }
	}
	Element li = html.createElement("li");
	ParagraphParser.parse(par, html, document, path, headerInfo, li);
	XWPFNumbering numbering = par.getDocument().getNumbering();
	// numbering.get
	CTNum num = numbering.getNum(par.getNumID()).getCTNum();
	CTAbstractNum abs = numbering.getAbstractNum(
		numbering.getNum(par.getNumID()).getCTNum().getAbstractNumId()
			.getVal()).getCTAbstractNum();
	int parIlvl = par.getCTP().getPPr().getNumPr().getIlvl().getVal()
		.intValue();
	int lastListId = getLastListId();
	if (listsIds.get(par.getNumID().intValue()) == null
		|| lastListId != par.getNumID().intValue()
		|| previousParIlvl < 0) {
	    Node parent = html.getElementsByTagName("body").item(0);
	    HashMap<Integer, ArrayList<Element>> subListsMap = listsIds.get(par
		    .getNumID().intValue()) != null ? listsIds.get(par
		    .getNumID().intValue())
		    : new HashMap<Integer, ArrayList<Element>>();
	    for (int i = 0; i <= parIlvl; i++) {

		int startat = 1;
		ArrayList<Element> subLists = new ArrayList<Element>();
		if ((lastListId != par.getNumID().intValue() || previousParIlvl < 0)
			&& listsIds.get(par.getNumID().intValue()) != null
			&& listsIds.get(par.getNumID().intValue()).get(i) != null) {
		    Element ol = listsIds
			    .get(par.getNumID().intValue())
			    .get(i)
			    .get(listsIds.get(par.getNumID().intValue()).get(i)
				    .size() - 1);
		    if (ol.getAttribute("start") != null) {
			startat = Integer.parseInt(ol.getAttribute("start"))
				+ ol.getChildNodes().getLength();
		    }
		    subLists = listsIds.get(par.getNumID().intValue()).get(i);
		}
		Element htmlList = this.createHTMLList(abs.getLvlArray(parIlvl)
			.getNumFmt().getVal().intValue() - 1, startat, html);

		subLists.add(htmlList);
		subListsMap.put(i, subLists);
		parent.appendChild(htmlList);
		parent = htmlList;
	    }
	    listsIds.remove(par.getNumID().intValue());
	    listsIds.put(par.getNumID().intValue(), subListsMap);
	}

	if (listsIds.get(par.getNumID().intValue()) != null) {
	    ArrayList<Element> subLists = listsIds.get(
		    par.getNumID().intValue()).get(parIlvl);
	    if (subLists != null
		    && (previousParIlvl >= parIlvl || previousParIlvl < 0)) {
		subLists.get(subLists.size() - 1).appendChild(li);
	    } else if (previousParIlvl < parIlvl) {
		int i = 100;
		for (i = parIlvl - 1; i > -1; i--) {
		    if (listsIds.get(par.getNumID().intValue()).get(i) != null) {
			Element htmlList = this.createHTMLList(
				abs.getLvlArray(parIlvl).getNumFmt().getVal()
					.intValue() - 1, 1, html);
			subLists = new ArrayList<Element>();
			subLists.add(htmlList);
			listsIds.get(par.getNumID().intValue()).put(parIlvl,
				subLists);
			htmlList.appendChild(li);
			subLists = listsIds.get(par.getNumID().intValue()).get(
				i);
			subLists.get(subLists.size() - 1).appendChild(htmlList);
			break;
		    }
		}
	    }
	}
	previousParIlvl = parIlvl;
    }

    private int getLastListId() {
	int lastListId = 0;
	for (Iterator<Integer> iter = listsIds.keySet().iterator(); iter
		.hasNext();) {
	    lastListId = iter.next();
	}
	return lastListId;
    }
}
