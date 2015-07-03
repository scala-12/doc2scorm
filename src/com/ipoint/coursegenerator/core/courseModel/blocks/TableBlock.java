package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.TableItem;

/**
 * Table block which includes rows
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TableBlock extends AbstractParagraphBlock {

	public TableBlock(List<TableItem> rows) {
		super(rows);
	}

	@Override
	public List<TableItem> getItems() {
		return (List<TableItem>) super.getItems();
	}

	/**
	 * @return html-element table
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element table = creatorTags.createElement("table");
		Element tBody = creatorTags.createElement("tbody");
		table.appendChild(tBody);

		for (TableItem row : this.getItems()) {
			tBody.appendChild(row.toHtml(creatorTags));
		}

		return table;
	}

}
