package com.ipoint.coursegenerator.core.courseModel.blocks.tabular;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.tabular.cell.CellBlock;

/**
 * This item includes {@link CellBlock}).
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TableItem extends AbstractItem<List<CellBlock>> {

	/**
	 * Create row as block item
	 * 
	 * @param cells
	 *            Cells of row. There cannot be null
	 */
	public TableItem(List<CellBlock> cells) {
		super(cells);
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
