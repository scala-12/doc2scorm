package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TableBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

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
		ArrayList<List<XWPFRun>> items = new ArrayList<List<XWPFRun>>();
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			Integer last = null; // number of last element in run
			if (run.getClass().equals(XWPFHyperlinkRun.class)) {
				last = i
						+ run.getCTR().getDomNode().getParentNode()
								.getChildNodes().getLength();
			}
			if (last == null) {
				for (last = i; (last < runs.size())
						&& !runs.get(last).getClass()
								.equals(XWPFHyperlinkRun.class); last++) {
					// waiting
				}
			}
			items.add(runs.subList(i, last));
			i = last - 1;
		}

		return items;
	}

	/**
	 * Checking that is paragraph list item
	 * 
	 * @param par
	 *            Paragraph
	 * @return true if this paragraph is item of list
	 */
	public static boolean isListElement(XWPFParagraph par) {
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

	@Override
	public AbstractBlock parseDoc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AbstractBlock> parseDocx(Object element) {
		if (element != null) { // if nothing
			ArrayList<AbstractBlock> paragraphs = new ArrayList<AbstractBlock>();

			if (element.getClass().equals(XWPFParagraph.class)) {
				// is text
				for (List<XWPFRun> runList : preParseParagraphOnPieces((XWPFParagraph) element)) {
					AbstractBlock block = null;
					if (runList.get(0).getClass()
							.equals(XWPFHyperlinkRun.class)) {
						block = new HyperlinkParser().parseDocx(runList);
					} else {
						block = new TextParser().parseDocx(runList);
					}
					paragraphs.add(block);
				}
			} else if ((element.getClass().equals(List.class))
					|| (element.getClass().equals(ArrayList.class))) {
				// is List
				paragraphs.add(new ListParser()
						.parseDocx((List<XWPFParagraph>) element));
			} else if (element.getClass().equals(XWPFTable.class)) {
				// is table
				paragraphs
						.add(new TableParser().parseDocx((XWPFTable) element));
			} else {
				paragraphs = null; // else
			}

			return paragraphs;
		} else {
			return null;
		}
	}

}
