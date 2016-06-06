package com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser;

import java.util.logging.Logger;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;

import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.HeaderBlock;

/**
 * Parsing to {@link HeaderBlock}
 *
 * @author Kalashnikov Vladislav
 */
public class HeaderParser extends ParagraphParser {

	private final static Logger log = Logger.getLogger(HeaderParser.class.getName());

	private static final String HEADING_NAME = "heading";

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
			return new HeaderBlock(ParagraphParser.parse(par, null).getItems(), getHeaderLevel(par) - maxLevel);
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
		XWPFStyles styles = par.getDocument().getStyles();
		XWPFStyle basisStyle = styles.getStyle(par.getStyleID());

		Integer headerLvl = null;
		String headerNum = null;
		if (basisStyle != null) {
			String styleName = basisStyle.getName().toLowerCase();
			while (!("normal".equals(styleName) || styleName.startsWith(HEADING_NAME))) {
				// if style not based to headers or is not header then check
				// basis style
				styleName = styles.getStyle(basisStyle.getBasisStyleID()).getName().toLowerCase();
			}
			if (styleName.startsWith(HEADING_NAME)) {
				// if style relate to headers
				headerNum = styleName.substring(HEADING_NAME.length()).trim();
			}

			if (headerNum != null) {
				try {
					headerLvl = Integer.parseInt(headerNum);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}

		return headerLvl;
	}
}
