package com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final Pattern HEADER_PATTERN = Pattern.compile(".*(heading|заголовок)[\\s_](\\d+)$");

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
		if (basisStyle != null) {
			String styleName = basisStyle.getName().toLowerCase();
			Matcher headerMatcher;
			while (!(headerMatcher = HEADER_PATTERN.matcher(styleName)).matches()
					&& (basisStyle.getBasisStyleID() != null)) {
				// if style not based to headers or is not header then check
				// basis style
				basisStyle = styles.getStyle(basisStyle.getBasisStyleID());
				styleName = basisStyle.getName().toLowerCase();
			}

			if (headerMatcher.matches()) {
				// if style relate to headers
				try {
					headerLvl = Integer.parseInt(headerMatcher.group(headerMatcher.groupCount()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}

		return headerLvl;
	}
}
