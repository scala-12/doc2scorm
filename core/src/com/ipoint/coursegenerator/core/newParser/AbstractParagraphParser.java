package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.FormulaBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.FormulaItem;

/**
 * Class for parsing to {@link AbstractParagraphBlock}
 * 
 * @author Kalashnikov Vladslav
 *
 */
public abstract class AbstractParagraphParser extends AbstractParser {

	/**
	 * Parsing to one {@link AbstractParagraphBlock} from list of
	 * {@link IBodyElement}
	 * 
	 * @param bodyElements
	 *            List of paragraphs. If first paragraph is not list then
	 *            parsing only it.
	 * @return {@link AbstractParagraphBlock} of paragraph
	 */
	public static AbstractParagraphBlock parse(List<IBodyElement> bodyElements) {
		AbstractParagraphBlock block = null;

		if (!bodyElements.isEmpty()) {
			if (bodyElements.get(0).getElementType()
					.equals(BodyElementType.PARAGRAPH)) {
				XWPFParagraph paragraph = (XWPFParagraph) bodyElements.get(0);

				if (!paragraph.getRuns().isEmpty()) {
					Integer size = ParagraphParser.listSize(0, paragraph,
							bodyElements);
					if (size == null) {
						// is simple text with image
						block = ParagraphParser
								.parse((XWPFParagraph) bodyElements.get(0));
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
						block = new FormulaBlock(new FormulaItem(paragraph
								.getCTP().getOMathParaList().get(0)
								.getOMathList().get(0)));
					}
				}
			} else if (bodyElements.get(0).getElementType()
					.equals(BodyElementType.TABLE)) {
				block = new TableParser().parse((XWPFTable) bodyElements
						.get(0));
			}
		}

		return block;
	}

}
