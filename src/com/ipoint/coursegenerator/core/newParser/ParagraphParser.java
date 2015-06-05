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
	public List<AbstractBlock> parseDocx(Object paragraphs) {
		if (paragraphs != null) { // if nothing
			if (paragraphs.getClass().equals(XWPFParagraph.class)) {
				return this.parseDocx((XWPFParagraph) paragraphs); // paragraph
			} else if ((paragraphs.getClass().equals(List.class))
					|| (paragraphs.getClass().equals(ArrayList.class))) {
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

	/**
	 * Parsing list elements
	 * 
	 * @param list
	 *            List items
	 * @return
	 */
	private List<AbstractBlock> parseDocx(List<XWPFParagraph> list) {
		ArrayList<AbstractBlock> paragraphs = new ArrayList<AbstractBlock>();
		paragraphs.add(new ListParser().parseDocx(list));

		return paragraphs;
	}

	/**
	 * Parsing table
	 * 
	 * @param docxTable
	 *            Table of Docx
	 * @return List which includes one {@link TableBlock}
	 */
	private List<AbstractBlock> parseDocx(XWPFTable docxTable) {
		ArrayList<AbstractBlock> paragraphs = new ArrayList<AbstractBlock>();
		paragraphs.add(new TableParser().parseDocx(docxTable));

		return paragraphs;
	}

	/**
	 * Parsing docx paragraph which includes only text and image
	 * 
	 * @param docxPar
	 *            Paragraph of docx
	 * @return List of {@link TextBlock}
	 */
	private List<AbstractBlock> parseDocx(XWPFParagraph docxPar) {
		ArrayList<AbstractBlock> paragraphs = new ArrayList<AbstractBlock>();
		for (List<XWPFRun> runList : preParseParagraphOnPieces((XWPFParagraph) docxPar)) {
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
