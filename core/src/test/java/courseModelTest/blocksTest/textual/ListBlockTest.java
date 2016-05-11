package test.java.courseModelTest.blocksTest.textual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import test.java.TestUtils;
import test.java.courseModelTest.blocksTest.AbstractBlockTest;

import com.ipoint.coursegenerator.core.courseModel.blocks.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.ListParser;

public class ListBlockTest extends AbstractBlockTest {

	@Override
	public void toHtml() {
		List<XWPFParagraph> lastList = null;
		for (List<XWPFParagraph> list : TestUtils.getTestListsParagraphs()) {
			ListBlock block = ListParser.parse(list.get(0),
					TestUtils.getMathMLFormulas());
			Element htmlBlock = block.toHtml(getHtmlDocument());

			if (lastList != null) {
				assertFalse(lastList.contains(list.get(0)));
			}

			if (block.getMarkerType() == ListBlock.SIMPLE_MARKER) {
				assertEquals(htmlBlock.getNodeName().toLowerCase(), "ul");
			} else {
				assertEquals(htmlBlock.getNodeName().toLowerCase(), "ol");

				if (block.getMarkerType() == ListBlock.UPPER_LETTER_MARKER) {
					assertEquals(htmlBlock.getAttribute("type"), "A");
				} else if (block.getMarkerType() == ListBlock.LOWER_LETTER_MARKER) {
					assertEquals(htmlBlock.getAttribute("type"), "a");
				} else if (block.getMarkerType() == ListBlock.UPPER_ROMAN_MARKER) {
					assertEquals(htmlBlock.getAttribute("type"), "I");
				} else if (block.getMarkerType() == ListBlock.LOWER_ROMAN_MARKER) {
					assertEquals(htmlBlock.getAttribute("type"), "i");
				} else if (block.getMarkerType() == ListBlock.DECIMAL_MARKER) {
					assertEquals(htmlBlock.getAttribute("type"), "1");
				}
			}

			for (Node node = htmlBlock.getFirstChild(); node != null; node = node
					.getNextSibling()) {
				assertEquals(node.getNodeName().toLowerCase(), "li");
			}

			lastList = list;
		}
	}
}
