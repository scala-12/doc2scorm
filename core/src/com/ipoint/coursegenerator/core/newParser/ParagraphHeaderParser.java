package com.ipoint.coursegenerator.core.newParser;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphHeaderBlock;

public class ParagraphHeaderParser extends ParagraphParser {

	public static ParagraphHeaderBlock parseDocx(XWPFParagraph paragraph,
			Integer level) {
		ParagraphHeaderBlock block = new ParagraphHeaderBlock(ParagraphParser
				.parseDocx(paragraph).getItems(), level);

		return block;
	}
}
