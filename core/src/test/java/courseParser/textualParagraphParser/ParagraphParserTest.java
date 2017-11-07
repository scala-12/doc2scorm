package test.java.courseParser.textualParagraphParser;

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

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.list.ListSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.HeaderParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.runs.TextRunItem;
import com.ipoint.coursegenerator.core.parsers.courseParser.AbstractParagraphParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser.HeaderInfo;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.ListParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.ParagraphParser;

import test.utils.TestTools;

public class ParagraphParserTest {

	@Test
	public void parseTestTextParagraphs() {
		for (XWPFParagraph par : TestTools.getTestTextParagraphs()) {
			String[] content = par.getText().split(":");

			if ((par.getText().indexOf(":") == par.getText().lastIndexOf(":")) && (content.length == 2)) {
				Set<Set<String>> stylesFromDoc = par.getRuns().stream().map(run -> TestTools.getRunStyles(run))
						.filter(style -> style != null).collect(Collectors.toSet());

				ParagraphBlock block = null;
				try {
					block = ParagraphParser.parse(par, TestTools.getMathMLFormulas());
				} catch (BlockCreationException | ItemCreationException e) {
					e.printStackTrace();
				}

				assertNotNull(block);

				assertEquals(block.getText(), content[0] + ":" + content[1]);

				// correct doc
				if (TestTools.SIMPLE_TEXT_PAR.equals(content[0])) {
					assertTrue(stylesFromDoc.isEmpty());
				} else if (TestTools.STYLED_TEXT_PAR.equals(content[0])) {
					Set<Set<String>> stylesFromText = Arrays.asList(content[1].split(";")).stream()
							.map(style -> new HashSet<String>(Arrays.asList(style.split(","))))
							.collect(Collectors.toSet());
					assertTrue(stylesFromDoc.containsAll(stylesFromText));

					block.getItems().get(0).getValue().getItems().stream()
							.map(item -> getItemStyles((TextRunItem) item)).collect(Collectors.toList())
							.containsAll(stylesFromText);
				}
			}
		}
	}

	public static Set<String> getItemStyles(TextRunItem item) {
		HashSet<String> result = new HashSet<>();
		if (item.isBold())
			result.add(TestTools.BOLD_TEXT);
		if (item.isItalic())
			result.add(TestTools.ITALIC_TEXT);
		if (item.isSuperscript())
			result.add(TestTools.SUPERSCRIPT_TEXT);
		if (item.isSubscript())
			result.add(TestTools.SUBSCRIPT_TEXT);
		if (item.isUnderline())
			result.add(TestTools.UNDERLINE_TEXT);
		if ((item.getColor() != null) && (item.getColor().isEmpty()))
			result.add(TestTools.COLORED_TEXT);

		return (result.isEmpty()) ? null : result;
	}

	@Test
	public void parseHeaderParagraphs() {
		for (XWPFParagraph par : TestTools.getOnlyTextParagraphs()) {
			if (TestTools.getHeaderParagraphs().contains(par)) {
				assertTrue(HeaderInfo.isHeader(par));
				HeaderParagraphBlock block = null;
				try {
					block = HeaderParser.parse(par, 0);
				} catch (BlockCreationException | ItemCreationException e) {
					e.printStackTrace();
				}

				assertNotNull(block);

				assertEquals(par.getText(), block.getText());
			} else {
				assertFalse(HeaderParser.HeaderInfo.isHeader(par));
			}
		}
	}

	@Test
	public void parseTextParagraphs() {
		for (XWPFParagraph par : TestTools.getOnlyTextParagraphs()) {
			ParagraphBlock block = null;
			try {
				block = ParagraphParser.parse(par, TestTools.getMathMLFormulas());
			} catch (BlockCreationException | ItemCreationException e) {
				e.printStackTrace();
			}

			assertNotNull(block);
			assertEquals(par.getText(), block.getText());
		}
	}

	@Test
	public void parseAllParagraphsAndTables() {
		for (int i = 0; i < TestTools.getTestDoc().getBodyElements().size(); i++) {
			IBodyElement elem = TestTools.getTestDoc().getBodyElements().get(i);
			if ((elem.getElementType().equals(BodyElementType.PARAGRAPH) && !((XWPFParagraph) elem).getText().isEmpty())
					|| elem.getElementType().equals(BodyElementType.TABLE)) {
				AbstractSectionBlock<?> block = null;
				try {
					block = AbstractParagraphParser.parse(elem, TestTools.getMathMLFormulas());
				} catch (BlockCreationException | ItemCreationException e) {
					e.printStackTrace();
				}

				assertNotNull(block);

				if (block instanceof ParagraphBlock) {
					assertEquals(((XWPFParagraph) elem).getText(), ((ParagraphBlock) block).getText());
				} else if (block instanceof ListSectionBlock) {
					// TODO: Change API - use one method for paragraphs
					// conversion in CourseParser (now is two similar)
					int iShift = ((ListSectionBlock) block).getSize();
					if (iShift > 0) {
						parseListParagraph(TestTools.getTestDoc().getBodyElements().subList(i, i + iShift).stream()
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
		for (List<XWPFParagraph> list : TestTools.getTestListsParagraphs()) {
			parseListParagraph(list);
		}
	}

	private static void parseListParagraph(List<XWPFParagraph> list) {
		ListSectionBlock block = null;
		try {
			block = ListParser.parse(list.get(0), TestTools.getMathMLFormulas());
		} catch (BlockCreationException | ItemCreationException e) {
			e.printStackTrace();
		}

		assertNotNull(block);

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
