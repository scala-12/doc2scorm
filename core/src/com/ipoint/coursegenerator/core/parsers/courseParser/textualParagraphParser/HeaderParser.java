package com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.HeaderSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;

/**
 * Parsing to {@link HeaderSectionBlock}
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
		private static final Pattern TEST_CORRECT_ANSWER_HEADER_PATTERN = Pattern
				.compile(".*(?:(?:correct[\\s_]answer)|(?:правильный[\\s_]ответ))");

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
			return isSomeHeader(new Pattern[] { SCO_THEORY_HEADER_PATTERN, SCO_TEST_HEADER_PATTERN }, par);
		}

		public static boolean isQuestion(XWPFParagraph par) {
			return isSomeHeader(new Pattern[] { TEST_QUESTION_HEADER_PATTERN }, par);
		}

		public static boolean isCorrectAnswer(XWPFParagraph par) {
			return isSomeHeader(new Pattern[] { TEST_CORRECT_ANSWER_HEADER_PATTERN }, par);
		}

		private static boolean isSomeHeader(Pattern[] patterns, XWPFParagraph par) {
			boolean result = false;

			if (hasStyle(par)) {
				XWPFStyle style = par.getDocument().getStyles().getStyle(par.getStyleID());
				for (Pattern pattern : patterns) {
					result |= getHeaderMatcher(pattern, style).matches();
				}
			}

			return result;
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
	 * Parsing to {@link HeaderSectionBlock} from {@link XWPFParagraph}
	 *
	 * @param par
	 *            Paragraph for parsing
	 * @param maxLevel
	 *            Max available level of header
	 * @return {@link HeaderSectionBlock}
	 */
	public static HeaderSectionBlock parse(XWPFParagraph par, int maxLevel)
			throws BlockCreationException, ItemCreationException {
		if (HeaderInfo.isHeader(par)) {
			HeaderInfo headerInfo = new HeaderInfo(par);

			return new HeaderSectionBlock(ParagraphParser.parse(par, null).getItems(),
					headerInfo.getLevel() - maxLevel);
		}

		return null;
	}
}
