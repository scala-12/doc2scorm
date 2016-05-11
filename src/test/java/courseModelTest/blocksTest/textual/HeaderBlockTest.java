package test.java.courseModelTest.blocksTest.textual;

import static org.junit.Assert.assertEquals;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Element;

import test.java.TestUtils;
import test.java.courseModelTest.blocksTest.AbstractBlockTest;

import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.HeaderBlock;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser;

public class HeaderBlockTest extends AbstractBlockTest {

	@Override
	public void toHtml() {
		for (XWPFParagraph par : TestUtils.getHeaderParagraphs()) {
			HeaderBlock block = HeaderParser.parse(par, 10);
			Element htmlBlock = block.toHtml(getHtmlDocument());

			assertEquals(
					htmlBlock.getNodeName().toLowerCase(),
					"h"
							+ String.valueOf(block.getLevel()
									+ HeaderBlock.LEVEL_OFFSET));
			assertEquals(htmlBlock.getTextContent(), block.getText());
		}
	}

}
