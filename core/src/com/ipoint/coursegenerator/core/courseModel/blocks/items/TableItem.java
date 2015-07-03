package com.ipoint.coursegenerator.core.courseModel.blocks.items;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This item includes {@link TableCellItem}).
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
	 *            Value of row. If there is null then return false
	 * @return If successful then true
	 */
	public boolean setValue(List<TableCellItem> cells) {
		if (cells != null) {
			if (!cells.isEmpty()) {
				this.value = cells;
				return true;
			}
		}

		return false;
	}

	/**
	 * @return html-element tr
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element tRow = creatorTags.createElement("tr");
		for (TableCellItem cell : this.getValue()) {
			tRow.appendChild(cell.toHtml(creatorTags));
		}

		return tRow;
	}

}
