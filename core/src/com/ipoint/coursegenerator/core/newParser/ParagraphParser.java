package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.ipoint.coursegenerator.core.internalCourse.blocks.ParagraphBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;
import com.ipoint.coursegenerator.core.internalCourse.items.ParagraphItem;

/**
 * Parsing paragraph whic includes tables, text, images, hyperlinks, list - is
 * an analogue paragraph in real life
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphParser extends AbstractParser {

	/**
	 * Parsing docx paragraph on pieces - simple text and hyperlink
	 * 
	 * @param paragraph
	 *            Paragraph for parsing
	 * @return List of text and hyperlink
	 */
	private static List<List<XWPFRun>> preParseParagraphOnPieces(
			XWPFParagraph paragraph) {
		ArrayList<List<XWPFRun>> runBlocks = new ArrayList<List<XWPFRun>>();
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			Integer last = null; // number of last element in run
			if (run instanceof XWPFHyperlinkRun) {
				last = i
						+ run.getCTR().getDomNode().getParentNode()
								.getChildNodes().getLength();
			}
			if (last == null) {
				for (last = i; (last < runs.size())
						&& !(runs.get(last) instanceof XWPFHyperlinkRun); last++) {
					// waiting
				}
			}
			runBlocks.add(runs.subList(i, last));
			i = last - 1;
		}

		return runBlocks;
	}

	/**
	 * Checking that is paragraph list item
	 * 
	 * @param par
	 *            Paragraph
	 * @return true if this paragraph is item of list
	 */
	private static boolean isListElement(XWPFParagraph par) {
		if ((par.getStyleID() == null) && (par.getNumID() != null)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Search end of list
	 * 
	 * @param start
	 *            Number of first item in list
	 * @param head
	 *            Item in list with number
	 * @return Count of list items of list
	 */
	public static Integer listSize(int start, XWPFParagraph head,
			List<IBodyElement> elements) {
		int size = 1;
		if (isListElement(head)) {
			if (head == elements.get(start)) {
				int listNumber = head.getNumID().intValue();
				int listLevel = head.getNumIlvl().intValue();
				boolean isAtomList = true;
				for (int i = start + 1; i < elements.size() && isAtomList; i++) {
					XWPFParagraph par = (XWPFParagraph) elements.get(i);
					isAtomList = ((par.getNumID() != null));
					if (isAtomList) {
						if ((par.getNumID().intValue() == listNumber)
								&& (par.getNumIlvl().intValue() == listLevel)) {
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
		} else {
			return null;
		}
	}

	/**
	 * Parse docx Paragraph to Paragraph block which includes only text
	 * 
	 * @param paragraph
	 *            Text paragraph
	 * @return Paragraph block
	 */
	public ParagraphBlock parseDocx(XWPFParagraph paragraph) {
		ArrayList<ParagraphItem> itemsOfParagraph = new ArrayList<ParagraphItem>();

		if (paragraph != null) { // if nothing
			if (paragraph instanceof XWPFParagraph) {
				// is text
				for (List<XWPFRun> runList : preParseParagraphOnPieces(paragraph)) {
					TextBlock block = null;
					if (runList.get(0) instanceof XWPFHyperlinkRun) {
						block = new HyperlinkParser().parseDocx(runList);
					} else {
						block = new TextParser().parseDocx(runList);
					}
					itemsOfParagraph.add(new ParagraphItem(block));
				}
			}
		}

		return (itemsOfParagraph.isEmpty()) ? null : new ParagraphBlock(
				itemsOfParagraph);
	}

}
