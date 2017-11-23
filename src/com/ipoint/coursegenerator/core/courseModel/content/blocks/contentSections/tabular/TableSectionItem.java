package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.cell.CellBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;

/**
 * This item includes {@link CellBlock}).
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TableSectionItem extends AbstractSectionItem<List<CellBlock>> {

	/**
	 * Create row as block item
	 * 
	 * @param cells
	 *            Cells of row. There cannot be null
	 */
	public TableSectionItem(List<CellBlock> cells) throws ItemCreationException {
		super(cells);
	}

	/**
	 * @return html-element tr
	 */
	@Override
	public Element toHtmlModel(Document creatorTags) {
		Element tRow = creatorTags.createElement("tr");
		for (CellBlock cell : this.getValue()) {
			tRow.appendChild(cell.toHtmlModel(creatorTags));
		}

		return tRow;
	}

	@Override
	public String getText() {
		StringBuilder text = new StringBuilder();
		for (CellBlock cell : this.getValue()) {
			text.append(' ').append(cell.getText());
		}

		return text.toString().trim();
	}

}
