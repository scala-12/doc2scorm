package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ParagraphBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TableBlock;
import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;
import com.ipoint.coursegenerator.core.internalCourse.items.TableCellItem;
import com.ipoint.coursegenerator.core.internalCourse.items.TableRowItem;

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

	@Override
	public TableBlock parseDocx(Object element) {
		XWPFTable table = (XWPFTable) element;
		ArrayList<AbstractItem> block = new ArrayList<AbstractItem>();

		for (int i = 0; i < table.getNumberOfRows(); i++) {
			XWPFTableRow tableRow = table.getRow(i);

			ArrayList<TableCellItem> cells = new ArrayList<TableCellItem>();
			for (int j = 0; j < tableRow.getTableCells().size(); j++) {
				XWPFTableCell tableCell = tableRow.getCell(j);
				TableCellItem cell = new TableCellItem();

				if (!tableCell.getParagraphs().isEmpty()) {
					ArrayList<ParagraphBlock> blocks = new ArrayList<ParagraphBlock>();
					for (int k = 0; k < tableCell.getParagraphs().size(); k++) {
						XWPFParagraph par = tableCell.getParagraphs().get(k);
						Integer size = ParagraphParser.listSize(k, par,
								tableCell.getBodyElements());
						if (size == null) {
							// is text
							if (!par.getText().isEmpty()) {
								blocks.add(new ParagraphBlock(
										new ParagraphParser().parseDocx(par)));
							}
						} else {
							// is list
							ArrayList<IBodyElement> listItems = new ArrayList<IBodyElement>();
							for (int blockNum = 0; blockNum < size; blockNum++, k++) {
								listItems.add(tableCell.getParagraphs().get(
										blockNum));
							}
							k--;
							blocks.add(new ParagraphBlock(new ParagraphParser()
									.parseDocx(listItems)));
						}

					}

					cell.setValue((blocks.isEmpty()) ? null : blocks);
				}

				if (tableCell.getCTTc().getTcPr().getGridSpan() != null) {
					cell.setColSpan(tableCell.getCTTc().getTcPr().getGridSpan()
							.getVal().intValue());
				}

				if (tableCell.getCTTc().getTcPr().getVMerge() != null) {
					if (tableCell.getCTTc().getTcPr().getVMerge().getVal() == STMerge.RESTART) {
						cell.setRowSpan(getRowSpan(table, i, j));
					}
				}
				cells.add(cell);
			}
			block.add(new TableRowItem(cells));
		}

		return new TableBlock(block);
	}

	@Override
	public AbstractBlock parseDoc() {
		// TODO Auto-generated method stub
		return null;
	}

}
