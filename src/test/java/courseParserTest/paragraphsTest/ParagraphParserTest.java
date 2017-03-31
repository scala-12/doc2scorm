package test.java.courseParserTest.paragraphsTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.Test;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.HeaderBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.items.TextContentItem;
import com.ipoint.coursegenerator.core.parsers.courseParser.AbstractParagraphParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser.HeaderInfo;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.ListParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.ParagraphParser;

import test.java.TestUtils;

public class ParagraphParserTest {

	@Test
	public void parseTestTextParagraphs() {
		for (XWPFParagraph par : TestUtils.getTestTextParagraphs()) {
			String[] content = par.getText().split(":");

			if ((par.getText().indexOf(":") == par.getText().lastIndexOf(":")) && (content.length == 2)) {
				Set<Set<String>> stylesFromDoc = par.getRuns().stream().map(run -> TestUtils.getRunStyles(run))
						.filter(style -> style != null).collect(Collectors.toSet());

				ParagraphBlock block = ParagraphParser.parse(par, TestUtils.getMathMLFormulas());

				assertEquals(block.getText(), content[0] + ":" + content[1]);

				// correct doc
				if (TestUtils.SIMPLE_TEXT_PAR.equals(content[0])) {
					assertTrue(stylesFromDoc.isEmpty());
				} else if (TestUtils.STYLED_TEXT_PAR.equals(content[0])) {
					Set<Set<String>> stylesFromText = Arrays.asList(content[1].split(";")).stream()
							.map(style -> new HashSet<String>(Arrays.asList(style.split(","))))
							.collect(Collectors.toSet());
					assertTrue(stylesFromDoc.containsAll(stylesFromText));

					block.getFirstItem().getValue().getItems().stream()
							.map(item -> getItemStyles((TextContentItem) item)).collect(Collectors.toList())
							.containsAll(stylesFromText);
				}
			}
		}
	}

	public static Set<String> getItemStyles(TextContentItem item) {
		HashSet<String> result = new HashSet<>();
		if (item.isBold())
			result.add(TestUtils.BOLD_TEXT);
		if (item.isItalic())
			result.add(TestUtils.ITALIC_TEXT);
		if (item.isSuperscript())
			result.add(TestUtils.SUPERSCRIPT_TEXT);
		if (item.isSubscript())
			result.add(TestUtils.SUBSCRIPT_TEXT);
		if (item.isUnderline())
			result.add(TestUtils.UNDERLINE_TEXT);
		if ((item.getColor() != null) && (item.getColor().isEmpty()))
			result.add(TestUtils.COLORED_TEXT);

		return (result.isEmpty()) ? null : result;
	}

	@Test
	public void parseHeaderParagraphs() {
		for (XWPFParagraph par : TestUtils.getOnlyTextParagraphs()) {
			if (TestUtils.getHeaderParagraphs().contains(par)) {
				assertTrue(HeaderInfo.isHeader(par));
				HeaderBlock block = HeaderParser.parse(par, 0);
				assertEquals(par.getText(), block.getText());
			} else {
				assertFalse(HeaderParser.HeaderInfo.isHeader(par));
			}
		}
	}

	@Test
	public void parseTextParagraphs() {
		for (XWPFParagraph par : TestUtils.getOnlyTextParagraphs()) {
			ParagraphBlock block = ParagraphParser.parse(par, TestUtils.getMathMLFormulas());
			assertNotNull(block);
			assertEquals(par.getText(), block.getText());
		}
	}

	@Test
	public void parseAllParagraphsAndTables() {
		for (int i = 0; i < TestUtils.getTestDoc().getBodyElements().size(); i++) {
			IBodyElement elem = TestUtils.getTestDoc().getBodyElements().get(i);
			if ((elem.getElementType().equals(BodyElementType.PARAGRAPH) && !((XWPFParagraph) elem).getText().isEmpty())
					|| elem.getElementType().equals(BodyElementType.TABLE)) {
				AbstractParagraphBlock<?> block = AbstractParagraphParser.parse(elem, TestUtils.getMathMLFormulas());

				assertNotNull(block);

				if (block instanceof ParagraphBlock) {
					assertEquals(((XWPFParagraph) elem).getText(), ((ParagraphBlock) block).getText());
				} else if (block instanceof ListBlock) {
					// TODO: Change API - use one method for paragraphs
					// conversion in CourseParser (now is two similar)
					int iShift = ((ListBlock) block).getSize();
					if (iShift > 0) {
						parseListParagraph(TestUtils.getTestDoc().getBodyElements().subList(i, i + iShift).stream()
								.filter(bodyElem -> bodyElem instanceof XWPFParagraph).map(par -> (XWPFParagraph) par)
								.collect(Collectors.toList()));
						i += iShift - 1;
					}
				}
			}
		}
	}

	@Test
	public void parseListParagraphs() {
		for (List<XWPFParagraph> list : TestUtils.getTestListsParagraphs()) {
			parseListParagraph(list);
		}
	}

	private static void parseListParagraph(List<XWPFParagraph> list) {
		ListBlock block = ListParser.parse(list.get(0), TestUtils.getMathMLFormulas());
		assertEquals(block.getItems().size(), list.size());

		for (int i = 0; i < list.size(); i++) {
			String itemValue = block.getItems().get(i).getValue().getText();
			assertEquals(list.get(i).getText(), itemValue);
			assertEquals(String.valueOf(i + 1), itemValue.split(";")[0]);
		}

		assertEquals(block.getText(),
				String.join("\n", list.stream().map(item -> item.getText()).collect(Collectors.toList())));
	}
}
