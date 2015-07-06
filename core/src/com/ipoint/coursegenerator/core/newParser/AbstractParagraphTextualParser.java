package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractTextualParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.FormulaBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.FormulaItem;

/**
 * Method for parsing to {@link AbstractTextualParagraphBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class AbstractParagraphTextualParser extends AbstractParagraphParser {

	/**
	 * Parsing from list of {@link IBodyElement} to
	 * {@link AbstractTextualParagraphBlock}
	 * 
	 * @param bodyElements
	 *            Elements of paragraph
	 * @return {@link AbstractTextualParagraphBlock}
	 */
	public static AbstractTextualParagraphBlock parse(
			List<IBodyElement> bodyElements) {
		XWPFParagraph paragraph = (XWPFParagraph) bodyElements.get(0);
		AbstractTextualParagraphBlock block = null;

		if (!paragraph.getRuns().isEmpty()) {
			Integer size = getListSize(0, paragraph, bodyElements);
			if (size == null) {
				// is simple text with image
				block = ParagraphParser.parse((XWPFParagraph) bodyElements
						.get(0));
			} else {
				// It is list
				ArrayList<XWPFParagraph> listItems = new ArrayList<XWPFParagraph>();
				for (int j = 0; j < size; j++) {
					listItems.add((XWPFParagraph) bodyElements.get(j));
				}
				block = ListParser.parse(listItems);
			}
		} else if (paragraph.getCTP() != null) {
			// TODO: formula as paragraph
			if (!paragraph.getCTP().getOMathParaList().isEmpty()) {
				block = new FormulaBlock(new FormulaItem(paragraph.getCTP()
						.getOMathParaList().get(0).getOMathList().get(0)));
			}
		}

		return block;
	}

	// TODO: check method which below
	/**
	 * Checking that is paragraph list item
	 * 
	 * @param par
	 *            Paragraph
	 * @return true if this paragraph is item of list
	 */
	private static boolean isListElement(XWPFParagraph par) {
		if ((par.getStyleID() != null) && (par.getNumID() != null)) {
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
	private static Integer getListSize(int start, XWPFParagraph head,
			List<IBodyElement> elements) {
		int size = 1;
		if (isListElement(head)) {
			if (head == elements.get(start)) {
				boolean isList = true;
				for (int i = start + 1; i < elements.size() && isList; ++i) {
					XWPFParagraph par = (XWPFParagraph) elements.get(i);
					isList = isListElement(par);
					if (isList) {
						size++;
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

}
