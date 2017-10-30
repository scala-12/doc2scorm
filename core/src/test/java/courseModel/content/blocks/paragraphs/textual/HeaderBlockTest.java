package test.java.courseModel.content.blocks.paragraphs.textual;

import static org.junit.Assert.assertEquals;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.HeaderParagraphBlock;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser;

import test.java.courseModel.content.blocks.paragraphs.AbstractBlockTest;
import test.utils.TestTools;

public class HeaderBlockTest extends AbstractBlockTest {

	@Override
	public void toHtml() {
		for (XWPFParagraph par : TestTools.getHeaderParagraphs()) {
			HeaderParagraphBlock block = HeaderParser.parse(par, 10);
			Element htmlBlock = block.toHtml(getHtmlDocument());

			assertEquals(htmlBlock.getNodeName().toLowerCase(),
					"h" + String.valueOf(block.getLevel() + HeaderParagraphBlock.LEVEL_OFFSET));
			assertEquals(htmlBlock.getTextContent(), block.getText());
		}
	}

}
