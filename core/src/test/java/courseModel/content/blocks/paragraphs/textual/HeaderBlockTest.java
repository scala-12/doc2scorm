package test.java.courseModel.content.blocks.paragraphs.textual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.HeaderSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser;

import test.java.courseModel.content.blocks.paragraphs.AbstractBlockTest;
import test.utils.TestTools;

public class HeaderBlockTest extends AbstractBlockTest {

	@Override
	public void toHtml() {
		for (XWPFParagraph par : TestTools.getHeaderParagraphs()) {
			HeaderSectionBlock block = null;
			try {
				block = HeaderParser.parse(par, 10);
			} catch (BlockCreationException | ItemCreationException e) {
				e.printStackTrace();
			}
			assertNotNull(block);
			Element htmlBlock = block.toHtmlModel(getHtmlDocument());

			assertEquals(htmlBlock.getNodeName().toLowerCase(),
					"h" + String.valueOf(block.getLevel() + HeaderSectionBlock.LEVEL_OFFSET));
			assertEquals(htmlBlock.getTextContent(), block.getText());
		}
	}

}
