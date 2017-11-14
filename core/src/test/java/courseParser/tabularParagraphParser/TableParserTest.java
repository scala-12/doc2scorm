package test.java.courseParser.tabularParagraphParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.Test;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.TableSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.TableSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.cell.CellBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.cell.CellItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.parsers.courseParser.tabularParagraphParser.TableParser;

import test.utils.TestTools;

public class TableParserTest {

	@Test
	public void parseTables() {
		for (XWPFTable table : TestTools.getTestTables()) {
			List<List<int[]>> tblInfo = TestTools.getSpecialTableContentCellsInfo(table);
			assertNotNull(tblInfo);

			TableSectionBlock block = null;
			try {
				block = TableParser.parse(table, TestTools.getMathMLFormulas());
			} catch (BlockCreationException | ItemCreationException e) {
				e.printStackTrace();
			}

			assertNotNull(block);

			List<TableSectionItem> rows = block.getItems();
			assertEquals(rows.size(), TestTools.getSpecialTableRowCount(table).intValue());

			for (int rowNum = TestTools.CONTENT_FROM_ROW; rowNum < rows.size(); rowNum++) {
				XWPFTableRow tRow = table.getRow(rowNum);
				List<CellBlock> cells = rows.get(rowNum).getValue();
				assertEquals(Integer.valueOf(cells.size()), TestTools.getSpecialCellCountInRow(tRow));

				for (int cellNum = TestTools.CONTENT_FROM_COLUMN; cellNum < cells.size(); cellNum++) {
					CellItem cell = cells.get(cellNum).getItem();
					assertNotNull(cell.getValue());

					assertEquals(rowNum, tblInfo.get(rowNum - 1).get(cellNum - 1)[0]);
					// can't check column number is difficult due to the use of
					// row span
					assertEquals(cell.getColSpan().intValue(), tblInfo.get(rowNum - 1).get(cellNum - 1)[2]);
					assertEquals(cell.getRowSpan().intValue(), tblInfo.get(rowNum - 1).get(cellNum - 1)[3]);
				}
			}
		}
	}
}
