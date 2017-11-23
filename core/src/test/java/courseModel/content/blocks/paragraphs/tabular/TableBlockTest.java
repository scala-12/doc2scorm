package test.java.courseModel.content.blocks.paragraphs.tabular;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.TableSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.cell.CellBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.cell.CellItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.ParagraphSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.parsers.courseParser.tabularParagraphParser.TableParser;

import test.java.courseModel.content.blocks.paragraphs.AbstractBlockTest;
import test.utils.TestTools;

public class TableBlockTest extends AbstractBlockTest {

	/**
	 * Check for
	 * <ol>
	 * <li>tag name
	 * <ul>
	 * <li>table</li>
	 * <li>tbody</li>
	 * <li>tr</li>
	 * <li>td</li>
	 * </ul>
	 * <li>row count of table;</li>
	 * <li>cell count of row</li>
	 * <li>cells of row
	 * <ul>
	 * <li>rowspan</li>
	 * <li>colspan</li>
	 * <li>text content</li>
	 * </ul>
	 * <ol>
	 */
	@Override
	public void toHtml() {
		for (XWPFTable table : TestTools.getTestTables()) {
			TableSectionBlock block = null;
			try {
				block = TableParser.parse(table, TestTools.getMathMLFormulas());
			} catch (BlockCreationException | ItemCreationException e) {
				e.printStackTrace();
			}

			assertNotNull(block);

			Element htmlBlock = block.toHtmlModel(getHtmlDocument());
			Node htmlTBody = htmlBlock.getFirstChild();

			assertEquals(htmlBlock.getNodeName().toLowerCase(), "table");
			assertEquals(htmlTBody.getNodeName().toLowerCase(), "tbody");
			assertEquals(htmlTBody.getChildNodes().getLength(), block.getItems().size());

			for (int rowNum = TestTools.CONTENT_FROM_ROW; rowNum < htmlTBody.getChildNodes().getLength(); rowNum++) {
				Node htmlRow = htmlTBody.getChildNodes().item(rowNum);
				List<CellBlock> row = block.getItems().get(rowNum).getValue();

				assertEquals(htmlRow.getNodeName().toLowerCase(), "tr");
				assertEquals(htmlRow.getChildNodes().getLength(), row.size());

				for (int cellNum = TestTools.CONTENT_FROM_COLUMN; cellNum < htmlRow.getChildNodes()
						.getLength(); cellNum++) {
					CellItem cell = row.get(cellNum).getItem();
					Element htmlCell = (Element) htmlRow.getChildNodes().item(cellNum);

					assertEquals(htmlCell.getNodeName().toLowerCase(), "td");

					// column and row span from model
					assertEquals(htmlCell.getAttribute("colspan"),
							(cell.getColSpan() == 1) ? "" : cell.getColSpan().toString());
					assertEquals(htmlCell.getAttribute("rowspan"),
							(cell.getRowSpan() == 1) ? "" : cell.getRowSpan().toString());

					if (cell.getValue().isPresent()) {
						for (AbstractSectionBlock<?> content : cell.getValue().get()) {
							if (content instanceof ParagraphSectionBlock) {
								assertEquals(htmlCell.getTextContent(), ((ParagraphSectionBlock) content).getText());
							}
						}
					} else {
						assertTrue(htmlCell.getTextContent().isEmpty());
					}
				}
			}
		}
	}
}
