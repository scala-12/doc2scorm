package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.List;

import com.ipoint.coursegenerator.core.internalCourse.blocks.TableBlock;

/**
 * Item for {@link TableBlock}. This item includes table cells (see
 * {@link TableCellItem}).
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TableItem extends AbstractItem {

	private List<TableCellItem> value;

	/**
	 * Create row as block item
	 * 
	 * @param cells
	 *            Cells of row. There cannot be null
	 */
	public TableItem(List<TableCellItem> cells) {
		if (!setValue(cells)) {
			// TODO:exception
		}
	}

	public List<TableCellItem> getValue() {
		return this.value;
	}

	/**
	 * Set value of row
	 * 
	 * @param cells
	 *            Value of row. There cannot be null
	 * @return If successful ten true
	 */
	public boolean setValue(List<TableCellItem> cells) {
		if (cells != null) {
			if (cells.isEmpty()) {
				return false;
			} else {
				this.value = cells;
				return true;
			}
		} else {
			return false;
		}
	}

}
