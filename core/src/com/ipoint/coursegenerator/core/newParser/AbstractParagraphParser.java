package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractParagraphBlock;

public abstract class AbstractParagraphParser extends AbstractParser {

	public static AbstractParagraphBlock parse(List<IBodyElement> bodyElements) {
		AbstractParagraphBlock block = null;

		if (!bodyElements.isEmpty()) {
			if (bodyElements.get(0).getElementType()
					.equals(BodyElementType.PARAGRAPH)) {
				// element docx as paragraph with runs
				XWPFParagraph paragraph = (XWPFParagraph) bodyElements.get(0);

				if (!paragraph.getRuns().isEmpty()) {
					Integer size = ParagraphParser.listSize(0, paragraph,
							bodyElements);
					if (size == null) {
						// is simple text with image
						block = new ParagraphParser()
								.parseDocx((XWPFParagraph) bodyElements.get(0));
					} else {
						// It is list
						ArrayList<XWPFParagraph> listItems = new ArrayList<XWPFParagraph>();
						for (int j = 0; j < size; j++) {
							listItems.add((XWPFParagraph) bodyElements.get(j));
						}
						block = new ListParser().parseDocx(listItems);
					}
				}
			} else if (bodyElements.get(0).getElementType()
					.equals(BodyElementType.TABLE)) {
				block = new TableParser().parseDocx((XWPFTable) bodyElements
						.get(0));
			}
		}

		return block;
	}
}
