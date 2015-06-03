package com.ipoint.coursegenerator.core.newParser;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TableBlock;
import com.ipoint.coursegenerator.core.internalCourse.items.TableCellItem;
import com.ipoint.coursegenerator.core.internalCourse.items.TableRowItem;

public class TableParser extends AbstractParser {

	private static Integer getRowSpan(XWPFTable tbl, int row, int col) {
		int factCell = col;
		for (int j = 0; j < col; j++) {
			if (tbl.getRow(row).getCell(j).getCTTc().getTcPr().getGridSpan() != null) {
				factCell += tbl.getRow(row).getCell(j).getCTTc().getTcPr()
						.getGridSpan().getVal().intValue();
			}
		}

		int rowSpan = 1;
		for (int i = row + 1, factNum = factCell; (i < tbl.getNumberOfRows())
				&& (factNum == factCell); i++) {
			factNum = 0;
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
		TableBlock block = new TableBlock();

		for (int i = 0; i < table.getNumberOfRows(); i++) {

			XWPFTableRow tableRow = table.getRow(i);
			TableRowItem row = new TableRowItem();
			for (int j = 0; j < tableRow.getTableCells().size(); j++) {

				XWPFTableCell tableCell = tableRow.getCell(j);
				TableCellItem cell = new TableCellItem();

				if (tableCell.getCTTc().getTcPr().getGridSpan() != null) {
					cell.setColSpan(tableCell.getCTTc().getTcPr().getGridSpan()
							.getVal().intValue());
				}

				if (tableCell.getCTTc().getTcPr().getVMerge() != null) {
					if (tableCell.getCTTc().getTcPr().getVMerge().getVal() == STMerge.RESTART) {
						cell.setRowSpan(getRowSpan(table, i, j));
					}
				}
				row.addCell(cell);
			}
			block.addItem(row);
		}

		return block;
	}

	public Integer offsetTable() {
		return null;
	}

	@Override
	public AbstractBlock parseDoc() {
		// TODO Auto-generated method stub
		return null;
	}

}
