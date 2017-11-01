package test.java.courseModel.content.blocks.paragraphs.textual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.list.ListSectionBlock;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.ListParser;

import test.java.courseModel.content.blocks.paragraphs.AbstractBlockTest;
import test.utils.TestTools;

public class ListBlockTest extends AbstractBlockTest {

	@Override
	public void toHtml() {
		List<XWPFParagraph> lastList = null;
		for (List<XWPFParagraph> list : TestTools.getTestListsParagraphs()) {
			ListSectionBlock block = null;
			try {
				block = ListParser.parse(list.get(0), TestTools.getMathMLFormulas());
			} catch (BlockCreationException | ItemCreationException e) {
				e.printStackTrace();
			}

			assertNotNull(block);

			Element htmlBlock = block.toHtml(getHtmlDocument());

			if (lastList != null) {
				assertFalse(lastList.contains(list.get(0)));
			}

			if (block.getMarkerType() == ListSectionBlock.SIMPLE_MARKER) {
				assertEquals(htmlBlock.getNodeName().toLowerCase(), "ul");
			} else {
				assertEquals(htmlBlock.getNodeName().toLowerCase(), "ol");

				if (block.getMarkerType() == ListSectionBlock.UPPER_LETTER_MARKER) {
					assertEquals(htmlBlock.getAttribute("type"), "A");
				} else if (block.getMarkerType() == ListSectionBlock.LOWER_LETTER_MARKER) {
					assertEquals(htmlBlock.getAttribute("type"), "a");
				} else if (block.getMarkerType() == ListSectionBlock.UPPER_ROMAN_MARKER) {
					assertEquals(htmlBlock.getAttribute("type"), "I");
				} else if (block.getMarkerType() == ListSectionBlock.LOWER_ROMAN_MARKER) {
					assertEquals(htmlBlock.getAttribute("type"), "i");
				} else if (block.getMarkerType() == ListSectionBlock.DECIMAL_MARKER) {
					assertEquals(htmlBlock.getAttribute("type"), "1");
				}
			}

			for (Node node = htmlBlock.getFirstChild(); node != null; node = node.getNextSibling()) {
				assertEquals(node.getNodeName().toLowerCase(), "li");
			}

			lastList = list;
		}
	}
}
