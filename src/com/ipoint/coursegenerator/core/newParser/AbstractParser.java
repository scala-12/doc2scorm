package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public abstract class AbstractParser implements ForParser {

	protected static List<List<XWPFRun>> preParseParagraphOnPieces(
			XWPFParagraph paragraph) {
		ArrayList<List<XWPFRun>> items = new ArrayList<List<XWPFRun>>();
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			Integer end = null;
			if (run.getClass().equals(XWPFHyperlinkRun.class)) {
				end = i
						+ run.getCTR().getDomNode().getParentNode()
								.getChildNodes().getLength();
			}
			if (end == null) {
				for (end = i; (end < runs.size())
						&& !runs.get(end).getClass()
								.equals(XWPFHyperlinkRun.class); end++) {
					// waiting
				}
			}
			items.add(runs.subList(i, end));
			i = end - 1;
		}

		return items;
	}
}
