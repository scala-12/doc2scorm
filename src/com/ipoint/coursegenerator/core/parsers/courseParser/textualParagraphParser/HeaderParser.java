package com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.HeaderBlock;

/**
 * Parsing to {@link HeaderBlock}
 *
 * @author Kalashnikov Vladislav
 */
public class HeaderParser extends ParagraphParser {

	public static class HeaderInfo {
		private static final Pattern SCO_THEORY_HEADER_PATTERN = Pattern
				.compile(".*(?:heading|заголовок)[\\s_](\\d+)$");
		private static final Pattern SCO_TEST_HEADER_PATTERN = Pattern
				.compile(".*(?:(?:test(?:ing)?)|(?:тест(?:ирование)))[\\s_](\\d+)$");
		private static final Pattern TEST_QUESTION_HEADER_PATTERN = Pattern.compile(".*(?:quest(?:ion)?|вопрос)");

		private int level;
		private String title;
		private boolean theoryType;

		public static final int TOP_LEVEL = 1;

		private static boolean isEmptyRuns(List<XWPFRun> runs) {
			boolean isEmptyRuns = true;
			for (int i = 0; (i < runs.size()) && isEmptyRuns; ++i) {
				isEmptyRuns = runs.get(i).toString().trim().isEmpty();
			}

			return isEmptyRuns;
		}

		private HeaderInfo(XWPFParagraph par) {
			if (hasStyle(par)) {
				this.title = par.getText();
				this.theoryType = true;

				XWPFStyle style = par.getDocument().getStyles().getStyle(par.getStyleID());
				if (style != null) {
					Integer headerLvl = null;

					Matcher headerMatcher = getHeaderMatcher(SCO_TEST_HEADER_PATTERN, style);
					if (headerMatcher.matches()) {
						// is test header
						try {
							headerLvl = Integer.parseInt(headerMatcher.group(headerMatcher.groupCount()));
							this.theoryType = false;
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					} else {
						// is theory header
						headerMatcher = getHeaderMatcher(SCO_THEORY_HEADER_PATTERN, style);
						try {
							headerLvl = Integer.parseInt(headerMatcher.group(headerMatcher.groupCount()));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}

					if ((headerLvl == null) || (headerLvl < TOP_LEVEL)) {
						this.level = TOP_LEVEL;
					} else {
						this.level = headerLvl;
					}
				}
			} else {
				// TODO: exception "is not header"
			}
		}

		public static HeaderInfo getHeaderInfo(XWPFParagraph par) {
			return new HeaderInfo(par);
		}

		private static Matcher getHeaderMatcher(Pattern pattern, XWPFStyle style) {
			XWPFStyles styles = style.getStyles();
			XWPFStyle currentBasisStyle = style;
			String styleName = currentBasisStyle.getName().toLowerCase();
			Matcher headerMatcher;
			while (!(headerMatcher = pattern.matcher(styleName)).matches()
					&& (currentBasisStyle.getBasisStyleID() != null)) {
				// if style not based to headers or is not header then check
				// basis style
				currentBasisStyle = styles.getStyle(currentBasisStyle.getBasisStyleID());
				styleName = currentBasisStyle.getName().toLowerCase();
			}

			return headerMatcher;
		}

		private static boolean hasStyle(XWPFParagraph par) {
			return !par.getRuns().isEmpty() && (par.getStyleID() != null) && !isEmptyRuns(par.getRuns());
		}

		public static boolean isHeader(XWPFParagraph par) {
			if (hasStyle(par)) {
				XWPFStyle style = par.getDocument().getStyles().getStyle(par.getStyleID());

				return getHeaderMatcher(SCO_THEORY_HEADER_PATTERN, style).matches()
						|| getHeaderMatcher(SCO_TEST_HEADER_PATTERN, style).matches();
			}

			return false;
		}

		public static boolean isQuestion(XWPFParagraph par) {
			if (hasStyle(par)) {
				XWPFStyle style = par.getDocument().getStyles().getStyle(par.getStyleID());

				return getHeaderMatcher(TEST_QUESTION_HEADER_PATTERN, style).matches();
			}

			return false;
		}

		public Integer getLevel() {
			return this.level;
		}

		public String getTitle() {
			return this.title;
		}

		public boolean isTheoryNoneTestHeader() {
			return theoryType;
		}
	}

	/**
	 * Parsing to {@link HeaderBlock} from {@link XWPFParagraph}
	 *
	 * @param par
	 *            Paragraph for parsing
	 * @param maxLevel
	 *            Max available level of header
	 * @return {@link HeaderBlock}
	 */
	public static HeaderBlock parse(XWPFParagraph par, int maxLevel) {
		if (HeaderInfo.isHeader(par)) {
			HeaderInfo headerInfo = new HeaderInfo(par);

			return new HeaderBlock(ParagraphParser.parse(par, null).getItems(), headerInfo.getLevel() - maxLevel);
		}

		return null;
	}
}
