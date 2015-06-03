package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.ArrayList;
import java.util.List;

public class TableRowItem extends AbstractItem {

	private ArrayList<TableCellItem> cells;

	public TableRowItem() {
		this.cells = new ArrayList<TableCellItem>();
	}

	@Override
	public List<TableCellItem> getValue() {
		return this.cells;
	}

	@Override
	public void setValue(Object cellList) {
		this.cells = (ArrayList<TableCellItem>) cellList;
	}

	public void addCell(TableCellItem cell) {
		this.cells.add(cell);
	}

}
