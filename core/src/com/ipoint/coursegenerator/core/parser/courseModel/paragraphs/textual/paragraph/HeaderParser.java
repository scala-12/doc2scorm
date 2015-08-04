package com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.textual.paragraph;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.HeaderBlock;

/**
 * Parsing to {@link HeaderBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class HeaderParser extends ParagraphParser {

	/**
	 * Parsing to {@link HeaderBlock} from {@link XWPFParagraph}
	 * 
	 * @param paragraph
	 *            Paragraph for parsing
	 * @param level
	 *            Level of header
	 * @return {@link HeaderBlock}
	 */
	public static HeaderBlock parse(XWPFParagraph paragraph, Integer level) {
		HeaderBlock block = new HeaderBlock(ParagraphParser.parse(paragraph,
				null).getItems(), level);

		return block;
	}
}
