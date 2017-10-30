package test.java.courseModel.content.blocks.paragraphs.textual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.ParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.HyperlinkRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.runs.FormulaRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.runs.TextRunItem;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.ParagraphParser;

import test.java.courseModel.content.blocks.paragraphs.AbstractBlockTest;
import test.java.courseParser.textualParagraphParser.ParagraphParserTest;
import test.utils.TestTools;

@RunWith(Parameterized.class)
public class ParagraphBlockTest extends AbstractBlockTest {

	private List<XWPFParagraph> pars;

	public ParagraphBlockTest(List<XWPFParagraph> pars) {
		this.pars = pars;
	}

	@SuppressWarnings("rawtypes")
	@Parameters
	public static Collection data() {
		return Arrays.asList(new Object[][] { { TestTools.getTestTextParagraphs() }, { TestTools.getTestHyperlinks() },
				{ TestTools.getOnlyTextParagraphs() } });
	}

	@Override
	public void toHtml() {
		for (XWPFParagraph par : this.pars) {
			ParagraphBlock block = ParagraphParser.parse(par, TestTools.getMathMLFormulas());
			Element htmlBlock = block.toHtml(getHtmlDocument());

			String blockText = block.getText();
			Set<Set<String>> htmlStyles = getHtmlStyles((Element) htmlBlock.getFirstChild());

			assertEquals(htmlBlock.getNodeName().toLowerCase(), "p");
			if (0 == block.getItems().stream()
					.map(pItem -> pItem.getValue().getItems().stream()
							.filter(cItem -> cItem instanceof FormulaRunItem).findFirst().isPresent())
					.filter(hasFormula -> hasFormula).count()) {
				assertEquals(htmlBlock.getTextContent(), blockText);
				// from doc
				assertEquals(htmlBlock.getTextContent(), par.getText());
			}

			// search special paragraphs with styles
			String[] content = blockText.split(":");
			if ((content.length == 2) && (content[0].indexOf(' ') == -1)) {
				if (TestTools.SIMPLE_TEXT_PAR.equals(content[0])) {
					assertEquals(htmlBlock.getFirstChild().getNodeName().toLowerCase(), "span");
					assertEquals(htmlBlock.getFirstChild().getFirstChild().getNodeName().toLowerCase(), "span");
				} else if (TestTools.STYLED_TEXT_PAR.equals(content[0])) {
					for (ParagraphItem pItem : block.getItems()) {
						assertTrue(htmlStyles.containsAll(
								pItem.getValue().getItems().stream().filter(item -> item instanceof TextRunItem)
										.map(item -> ParagraphParserTest.getItemStyles((TextRunItem) item))
										.filter(item -> item != null).collect(Collectors.toSet())));
					}
				}
			}

			boolean isHyperlinkTest = TestTools.getTestHyperlinks().contains(par);
			assertTrue(getHtmlLinks(htmlBlock, isHyperlinkTest).equals(getBlockLinks(block, isHyperlinkTest)));
		}
	}

	private static List<String> getHtmlLinks(Element htmlBlock, boolean textIsLink) {
		ArrayList<String> links = new ArrayList<>();
		for (Element node = (Element) htmlBlock.getFirstChild(); node != null; node = (Element) node.getNextSibling()) {
			if (node.getNodeName().toLowerCase().equals("a")) {
				assertEquals(node.getAttribute("target"), "_blank");
				if (textIsLink) {
					assertEquals(node.getAttribute("href"), node.getTextContent());
				}
				links.add(node.getAttribute("href"));
			}
		}

		return links;
	}

	private static List<String> getBlockLinks(ParagraphBlock block, boolean textIsLink) {
		ArrayList<String> links = new ArrayList<>();
		for (ParagraphItem pItem : block.getItems()) {
			if (pItem.getValue() instanceof HyperlinkRunsBlock) {
				HyperlinkRunsBlock linkBlock = (HyperlinkRunsBlock) pItem.getValue();
				if (textIsLink) {
					assertTrue(block.getText().contains(linkBlock.getUrl()));
				}
				links.add(linkBlock.getUrl());
			}
		}

		return links;
	}

	private static Set<Set<String>> getHtmlStyles(Element text) {
		HashSet<Set<String>> result = new HashSet<>();

		NodeList nodes = text.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Element node = (Element) nodes.item(i);
			HashSet<String> styles = new HashSet<>();
			if (!"span".equalsIgnoreCase(node.getNodeName())) {
				styles.add(getNodeStyle(node));
			}
			while (node.hasChildNodes() && !"#text".equalsIgnoreCase(node.getFirstChild().getNodeName())) {
				node = (Element) node.getFirstChild();
				String style = getNodeStyle(node);
				if (style != null) {
					styles.add(style);
				}
			}
			if (!styles.isEmpty()) {
				result.add(styles);
			}
		}

		return (result.isEmpty()) ? null : result;
	}

	private static String getNodeStyle(Element node) {
		if (node.getNodeName() != null) {
			if ("b".equalsIgnoreCase(node.getNodeName()) || "strong".equalsIgnoreCase(node.getNodeName()))
				return TestTools.BOLD_TEXT;
			if ("i".equalsIgnoreCase(node.getNodeName()))
				return TestTools.ITALIC_TEXT;
			if ("sup".equalsIgnoreCase(node.getNodeName()))
				return TestTools.SUPERSCRIPT_TEXT;
			if ("sub".equalsIgnoreCase(node.getNodeName()))
				return TestTools.SUBSCRIPT_TEXT;
			if ("u".equalsIgnoreCase(node.getNodeName()))
				return TestTools.UNDERLINE_TEXT;
			if ("font".equalsIgnoreCase(node.getNodeName()) && (node.getAttribute("color") != null))
				return TestTools.COLORED_TEXT;
		}

		return null;
	}
}
