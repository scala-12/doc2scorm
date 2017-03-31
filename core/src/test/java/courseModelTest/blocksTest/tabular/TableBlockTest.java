package test.java.courseModelTest.blocksTest.tabular;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular.cell.CellBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular.cell.CellItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.parsers.courseParser.tabularParagraphParser.TableParser;

import test.java.TestUtils;
import test.java.courseModelTest.blocksTest.AbstractBlockTest;

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
		for (XWPFTable table : TestUtils.getTestTables()) {
			TableBlock block = TableParser.parse(table, TestUtils.getMathMLFormulas());
			Element htmlBlock = block.toHtml(getHtmlDocument());
			Node htmlTBody = htmlBlock.getFirstChild();

			assertEquals(htmlBlock.getNodeName().toLowerCase(), "table");
			assertEquals(htmlTBody.getNodeName().toLowerCase(), "tbody");
			assertEquals(htmlTBody.getChildNodes().getLength(), block.getItems().size());

			for (int rowNum = TestUtils.CONTENT_FROM_ROW; rowNum < htmlTBody.getChildNodes().getLength(); rowNum++) {
				Node htmlRow = htmlTBody.getChildNodes().item(rowNum);
				List<CellBlock> row = block.getItems().get(rowNum).getValue();

				assertEquals(htmlRow.getNodeName().toLowerCase(), "tr");
				assertEquals(htmlRow.getChildNodes().getLength(), row.size());

				for (int cellNum = TestUtils.CONTENT_FROM_COLUMN; cellNum < htmlRow.getChildNodes()
						.getLength(); cellNum++) {
					CellItem cell = row.get(cellNum).getFirstItem();
					Element htmlCell = (Element) htmlRow.getChildNodes().item(cellNum);

					assertEquals(htmlCell.getNodeName().toLowerCase(), "td");

					// column and row span from model
					assertEquals(htmlCell.getAttribute("colspan"),
							(cell.getColSpan() == 1) ? "" : cell.getColSpan().toString());
					assertEquals(htmlCell.getAttribute("rowspan"),
							(cell.getRowSpan() == 1) ? "" : cell.getRowSpan().toString());

					if (cell.getValue() == null) {
						assertTrue(htmlCell.getTextContent().isEmpty());
					} else {
						for (AbstractParagraphBlock<?> content : cell.getValue()) {
							if (content instanceof ParagraphBlock) {
								assertEquals(htmlCell.getTextContent(), ((ParagraphBlock) content).getText());
							}
						}
					}
				}
			}
		}
	}
}
