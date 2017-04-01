package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular.cell.CellBlock;

/**
 * This item includes {@link CellBlock}).
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TableItem extends AbstractParagraphItem<List<CellBlock>> {

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
		return toHtml(creatorTags, true);
	}

	@Override
	public Element toHtmlWithoutStyles(Document creatorTags) {
		return this.toHtml(creatorTags, false);
	}

	private Element toHtml(Document creatorTags, boolean styled) {
		Element tRow = creatorTags.createElement("tr");
		for (CellBlock cell : this.getValue()) {
			tRow.appendChild((styled) ? cell.toHtml(creatorTags) : cell.toHtmlWithoutStyles(creatorTags));
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
