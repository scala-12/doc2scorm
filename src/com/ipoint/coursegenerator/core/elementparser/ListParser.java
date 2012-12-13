package com.ipoint.coursegenerator.core.elementparser;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.hwpf.usermodel.HWPFList;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ListParser {
    private HashMap<Integer, HashMap<Integer, ArrayList<Element>>> listsIds = null;
    private int previousParIlvl = 0;

    public ListParser() {
	previousParIlvl = 0;
	listsIds = new HashMap<Integer, HashMap<Integer, ArrayList<Element>>>();
    }

    public Element createHTMLList(int numberFormat, Document html) {
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
	return htmlList;
    }

    public void parse(Object paragraph, Document html, Object document,
	    String path) {
	if (paragraph instanceof Paragraph) {
	    Paragraph par = (Paragraph) paragraph;
	    HWPFList list = par.getList();
	    Element li = html.createElement("li");
	    li.setTextContent(par.text());
	    if (listsIds.get(list.getLsid()) == null) {
		Element htmlList = this.createHTMLList(
			list.getNumberFormat((char) 0), html);
		ArrayList<Element> subLists = new ArrayList<Element>();
		subLists.add(htmlList);
		HashMap<Integer, ArrayList<Element>> subListsMap = new HashMap<Integer, ArrayList<Element>>();
		subListsMap.put(par.getIlvl(), subLists);
		listsIds.put(list.getLsid(), subListsMap);
		html.getElementsByTagName("body").item(0).appendChild(htmlList);
	    }
	   
	    if (listsIds.get(list.getLsid()) != null) {
		ArrayList<Element> subLists = listsIds.get(list.getLsid()).get(par.getIlvl());
		if (subLists != null && previousParIlvl >= par.getIlvl()  ) {
		    subLists.get(subLists.size() - 1).appendChild(li);
		} else if (previousParIlvl < par.getIlvl()) {
		    int i = 100;
		    for (i = par.getIlvl() - 1; i > -1; i--) {			
			if (listsIds.get(list.getLsid()).get(i) != null) {
			    Element htmlList = this.createHTMLList(
				    list.getNumberFormat((char) par.getIlvl()),
				    html);
			    subLists = new ArrayList<Element>();
			    subLists.add(htmlList);
			    listsIds.get(list.getLsid()).put(par.getIlvl(), subLists);
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
    }
}
