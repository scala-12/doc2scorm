package test.java.courseParserTest.paragraphsTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.Test;

import test.java.TestUtils;

import com.ipoint.coursegenerator.core.courseModel.blocks.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.tabular.TableItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.tabular.cell.CellBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.tabular.cell.CellItem;
import com.ipoint.coursegenerator.core.parsers.courseParser.tabularParagraphParser.TableParser;

public class TableParserTest {

	@Test
	public void parseTables() {
		for (XWPFTable table : TestUtils.getTestTables()) {
			List<List<int[]>> tblInfo = TestUtils
					.getSpecialTableContentCellsInfo(table);
			assertNotNull(tblInfo);

			TableBlock block = TableParser.parse(table,
					TestUtils.getMathMLFormulas());
			List<TableItem> rows = block.getItems();
			assertEquals(rows.size(), TestUtils.getSpecialTableRowCount(table)
					.intValue());

			for (int rowNum = TestUtils.CONTENT_FROM_ROW; rowNum < rows.size(); rowNum++) {
				XWPFTableRow tRow = table.getRow(rowNum);
				List<CellBlock> cells = rows.get(rowNum).getValue();
				assertEquals(Integer.valueOf(cells.size()),
						TestUtils.getSpecialCellCountInRow(tRow));

				for (int cellNum = TestUtils.CONTENT_FROM_COLUMN; cellNum < cells
						.size(); cellNum++) {
					assertEquals(cells.get(cellNum).getItems().size(), 1);
					CellItem cell = cells.get(cellNum).getFirstItem();
					assertNotNull(cell.getValue());

					assertEquals(rowNum,
							tblInfo.get(rowNum - 1).get(cellNum - 1)[0]);
					// can't check column number is difficult due to the use of
					// row span
					assertEquals(cell.getColSpan().intValue(),
							tblInfo.get(rowNum - 1).get(cellNum - 1)[2]);
					assertEquals(cell.getRowSpan().intValue(),
							tblInfo.get(rowNum - 1).get(cellNum - 1)[3]);
				}
			}
		}
	}
}
