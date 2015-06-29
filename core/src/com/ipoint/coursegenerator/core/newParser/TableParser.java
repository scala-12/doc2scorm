package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ListBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TableBlock;
import com.ipoint.coursegenerator.core.internalCourse.items.TableCellItem;
import com.ipoint.coursegenerator.core.internalCourse.items.TableItem;

/**
 * Parsing paragraph which includes only table
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
	 * Parse Table paragraph to Table Block
	 * 
	 * @param table
	 *            Table paragraph
	 * @return Table block
	 */
	public TableBlock parseDocx(XWPFTable table) {
		ArrayList<TableItem> block = new ArrayList<TableItem>();

		for (int i = 0; i < table.getNumberOfRows(); i++) {
			XWPFTableRow tableRow = table.getRow(i);

			ArrayList<TableCellItem> cells = new ArrayList<TableCellItem>();
			for (int j = 0; j < tableRow.getTableCells().size(); j++) {
				XWPFTableCell tableCell = tableRow.getCell(j);
				TableCellItem cell = new TableCellItem();
				
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
									.parse(tableCell.getBodyElements().subList(k,
											tableCell.getBodyElements().size()));

							if (paragraphBlock != null) {
								if (paragraphBlock instanceof ListBlock) {
									k += ((ListBlock) paragraphBlock).getItems().size() - 1;
								}

								blocks.add(paragraphBlock);
							}
						}

						cell.setValue(blocks);
					}
				}
				
				cells.add(cell);
			}
			block.add(new TableItem(cells));
		}

		return (block.isEmpty()) ? null : new TableBlock(block);
	}
}
