package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.TextBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.FormulaItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.ParagraphItem;

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
		
		ArrayList<Integer> formula = new ArrayList<Integer>();
		if (!paragraph.getCTP().getOMathList().isEmpty()) {
			Node node = paragraph.getCTP().getDomNode().getChildNodes().item(0);
			for (int i = 0; node != null; node = node.getNextSibling()) {
				if (node.getLocalName().equalsIgnoreCase("r")) {
					i++;
				} else if (node.getLocalName().equalsIgnoreCase("oMath")) {
					formula.add(i);
				}
			}
		}
		
		Integer k = (formula.isEmpty()) ? null
				: 0; 
		
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			Integer last = null; // number of last element in run
			if (run instanceof XWPFHyperlinkRun) {
				last = i
						+ run.getCTR().getDomNode().getParentNode()
								.getChildNodes().getLength();
			} else {
				for (last = i; (last < runs.size())
						&& !(runs.get(last) instanceof XWPFHyperlinkRun); last++) {
					// waiting
				}
			}
			
			if (k != null) {
				Integer start = i;
				
				while (start != null) {
					runBlocks.add(runs.subList(start, formula.get(k)));
					start = formula.get(k);
					runBlocks.add(null);
					for (; (k < formula.size() - 1) && (formula.get(k) != formula.get(k + 1)); k++) {
						runBlocks.add(null);
					}
					
					if ((formula.get(k) >= last) || (k <= (formula.size() - 1))) {
						start = null;
					}
				}
				
				runBlocks.add(runs.subList(formula.get(k), last));
			} else {
				runBlocks.add(runs.subList(i, last));
			}
			
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
		List<CTOMath> formuls = paragraph.getCTP().getOMathList();
		
		if (paragraph != null) {
			// is text
			int k = 0;
			for (List<XWPFRun> runList : preParseParagraphOnPieces(paragraph)) {
				TextBlock block = null;
				
				if (runList == null) {
					block = new TextParser().parseDocx(formuls.get(k++));
				} else if (runList.get(0) instanceof XWPFHyperlinkRun) {
					block = new HyperlinkParser().parseDocx(runList);
				} else {
					block = new TextParser().parseDocx(runList);
				}
				
				itemsOfParagraph.add(new ParagraphItem(block));
			}
		}

		return (itemsOfParagraph.isEmpty()) ? null : new ParagraphBlock(
				itemsOfParagraph);
	}

}
