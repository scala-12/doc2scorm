package com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.tabular;

import java.util.ArrayList;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.tabular.TableItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.tabular.tableCell.CellBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.tabular.tableCell.CellItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.parser.AbstractParser;
import com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.AbstractParagraphParser;

/**
 * Parsing to {@link TableBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TableParser extends AbstractParser {

	/**
	 * Search count of cells which combined in row
	 * 
	 * @param tbl
	 *            Table
	 * @param row
	 *            Row of cell
	 * @param col
	 *            Column of cell
	 * @return
	 */
	private static Integer getRowSpan(XWPFTable tbl, int row, int col) {
		int factCell = col; // The actual number of this cell in column
		for (int j = 0; j < col; j++) {
			if (tbl.getRow(row).getCell(j).getCTTc().getTcPr().getGridSpan() != null) {
				factCell += tbl.getRow(row).getCell(j).getCTTc().getTcPr()
						.getGridSpan().getVal().intValue();
			}
		}

		int rowSpan = 1;
		for (int i = row + 1, factNum = factCell; (i < tbl.getNumberOfRows())
				&& (factNum == factCell); i++) {
			factNum = 0; // The actual number of cell for compare
			XWPFTableRow tblRow = tbl.getRow(i);
			int j = 0;
			while ((j < tblRow.getTableCells().size()) && (factNum != factCell)) {
				if (tblRow.getCell(j).getCTTc().getTcPr().getGridSpan() != null) {
					factNum += tblRow.getCell(j).getCTTc().getTcPr()
							.getGridSpan().getVal().intValue();
				} else {
					factNum++;
				}
				j++;
			}
			if (factNum == factCell) {
				if (tblRow.getCell(j).getCTTc().getTcPr().getVMerge() != null) {
					rowSpan++;
				}
			}
		}

		return rowSpan;
	}

	/**
	 * Parse to {@link TableBlock} from {@link XWPFTable}
	 * 
	 * @param table
	 *            Table for parsing
	 * @return {@link TableBlock}
	 */
	public static TableBlock parse(XWPFTable table) {
		ArrayList<TableItem> block = new ArrayList<TableItem>();

		for (int i = 0; i < table.getNumberOfRows(); i++) {
			XWPFTableRow tableRow = table.getRow(i);

			ArrayList<CellBlock> cells = new ArrayList<CellBlock>();
			for (int j = 0; j < tableRow.getTableCells().size(); j++) {
				XWPFTableCell tableCell = tableRow.getCell(j);
				CellItem cell = new CellItem();

				if (tableCell.getCTTc().getTcPr().getVMerge() != null) {
					if (tableCell.getCTTc().getTcPr().getVMerge().getVal() == STMerge.RESTART) {
						cell.setRowSpan(getRowSpan(table, i, j));
					} else {
						cell.setFantom();
					}
				}

				if (cell.getRowSpan() != null) {
					if (tableCell.getCTTc().getTcPr().getGridSpan() != null) {
						cell.setColSpan(tableCell.getCTTc().getTcPr()
								.getGridSpan().getVal().intValue());
					}

					if (!tableCell.getBodyElements().isEmpty()) {
						ArrayList<AbstractParagraphBlock> blocks = new ArrayList<AbstractParagraphBlock>();
						for (int k = 0; k < tableCell.getBodyElements().size(); k++) {
							AbstractParagraphBlock paragraphBlock = AbstractParagraphParser
									.parse(tableCell.getBodyElements().subList(
											k,
											tableCell.getBodyElements().size()));

							if (paragraphBlock != null) {
								if (paragraphBlock instanceof ListBlock) {
									k += ((ListBlock) paragraphBlock)
											.getItems().size() - 1;
								}

								blocks.add(paragraphBlock);
							}
						}

						cell.setValue(blocks);
					}
				}

				cells.add(new CellBlock(cell));
			}
			block.add(new TableItem(cells));
		}

		return (block.isEmpty()) ? null : new TableBlock(block);
	}
}
