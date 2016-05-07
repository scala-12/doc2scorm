package com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser;

import java.util.logging.Logger;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.HeaderBlock;

/**
 * Parsing to {@link HeaderBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class HeaderParser extends ParagraphParser {

	private final static Logger log = Logger.getLogger(HeaderParser.class
			.getName());

	/**
	 * Parsing to {@link HeaderBlock} from {@link XWPFParagraph}
	 * 
	 * @param paragraph
	 *            Paragraph for parsing
	 * @param level
	 *            Level of header
	 * @return {@link HeaderBlock}
	 */
	public static HeaderBlock parse(XWPFParagraph par, int maxLevel) {
		Integer headerLevel = getHeaderLevel(par);
		if (headerLevel == null) {
			log.warning("Paragraph has not header marker");
		} else {
			return new HeaderBlock(ParagraphParser.parse(par, null).getItems(),
					getHeaderLevel(par) - maxLevel);
		}

		return null;
	}

	/**
	 * Search header level
	 * 
	 * @param headId
	 *            ID of header
	 * @return
	 */
	public static Integer getHeaderLevel(XWPFParagraph par) {
		String headId = par.getStyleID();

		Integer resultVariable;
		if (headId == null) {
			resultVariable = null;
		} else if (("Heading1".equals(headId)) || ("1".equals(headId))) {
			resultVariable = 1;
		} else if (("Heading2".equals(headId)) || ("2".equals(headId))) {
			resultVariable = 2;
		} else if (("Heading3".equals(headId)) || ("3".equals(headId))) {
			resultVariable = 3;
		} else if (("Heading4".equals(headId)) || ("4".equals(headId))) {
			resultVariable = 4;
		} else if (("Heading5".equals(headId)) || ("5".equals(headId))) {
			resultVariable = 5;
		} else if (("Heading6".equals(headId)) || ("6".equals(headId))) {
			resultVariable = 6;
		} else if (("Heading7".equals(headId)) || ("7".equals(headId))) {
			resultVariable = 7;
		} else if (("Heading8".equals(headId)) || ("8".equals(headId))) {
			resultVariable = 8;
		} else if (("Heading9".equals(headId)) || ("9".equals(headId))) {
			resultVariable = 9;
		} else {
			resultVariable = null;
		}

		return resultVariable;
	}
}
