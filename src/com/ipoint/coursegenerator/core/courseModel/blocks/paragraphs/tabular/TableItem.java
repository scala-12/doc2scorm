package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.tabular;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.tabular.tableCell.CellBlock;

/**
 * This item includes {@link CellBlock}).
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TableItem extends AbstractItem {

	private List<CellBlock> value;

	/**
	 * Create row as block item
	 * 
	 * @param cells
	 *            Cells of row. There cannot be null
	 */
	public TableItem(List<CellBlock> cells) {
		if (!setValue(cells)) {
			// TODO:exception
		}
	}

	public List<CellBlock> getValue() {
		return this.value;
	}

	/**
	 * Set value of row
	 * 
	 * @param cells
	 *            Value of row. If there is null then return false
	 * @return If successful then true
	 */
	public boolean setValue(List<CellBlock> cells) {
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
		for (CellBlock cell : this.getValue()) {
			tRow.appendChild(cell.toHtml(creatorTags));
		}

		return tRow;
	}

}
