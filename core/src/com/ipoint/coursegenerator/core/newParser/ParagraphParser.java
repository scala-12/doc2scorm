package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;

public class ParagraphParser extends AbstractParser {
	
	public static boolean isListElement(XWPFParagraph par) {
		if ((par.getStyleID() == null) && (par.getNumID() != null)) {
			return true;
		} else {
			return false;
		}
	}

	public static Integer listSize(int start, XWPFParagraph head) {
		int size = 1;
		if (isListElement(head)) {
			int listNumber = head.getNumID().intValue();
			XWPFDocument doc = head.getDocument();
			boolean isAtomList = true;
			for (int i = start + 1; i < doc.getBodyElements().size() && isAtomList; i++) {
				XWPFParagraph par = (XWPFParagraph) doc.getBodyElements().get(i);
				isAtomList = (par.getNumID() != null);
				if (isAtomList) {
					if (par.getNumID().intValue() == listNumber) {
						size++;
					} else {
						isAtomList = false;
					}
				}
			}
			return size;
		} else {
			return null;
		}
	}

	@Override
	public AbstractBlock parseDoc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AbstractBlock> parseDocx(Object paragraphs) {
		if (paragraphs != null) { // if nothing
			if (paragraphs.getClass().equals(XWPFParagraph.class)) {
				return this.parseDocx((XWPFParagraph) paragraphs); // paragraph
			} else if (paragraphs.getClass().equals(List.class)) {
				return this.parseDocx((List<XWPFParagraph>) paragraphs); // list
			} else if (paragraphs.getClass().equals(XWPFTable.class)) {
				return this.parseDocx((XWPFTable) paragraphs); // table
			} else {
				return null; // else
			}
		} else {
			return null;
		}
	}

	private List<AbstractBlock> parseDocx(List<XWPFParagraph> listParagraph) {
		return parseDocx(new ListParser().parseDocx(listParagraph));
	}

	private List<AbstractBlock> parseDocx(XWPFTable tableParagraph) {
		ArrayList<AbstractBlock> paragraphs = new ArrayList<AbstractBlock>();
		paragraphs.add(new TableParser().parseDocx(tableParagraph));

		return paragraphs;
	}

	private List<AbstractBlock> parseDocx(XWPFParagraph textParagraph) {
		ArrayList<AbstractBlock> paragraphs = new ArrayList<AbstractBlock>();
		for (List<XWPFRun> runList : AbstractParser
				.preParseParagraphOnPieces((XWPFParagraph) textParagraph)) {
			AbstractBlock block = null;
			if (runList.get(0).getClass().equals(XWPFHyperlinkRun.class)) {
				block = new HyperlinkParser().parseDocx(runList);
			} else {
				block = new TextParser().parseDocx(runList);
			}
			paragraphs.add(block);
		}

		return paragraphs;
	}

}
