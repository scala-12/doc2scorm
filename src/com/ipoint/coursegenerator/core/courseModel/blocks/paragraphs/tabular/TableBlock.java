package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.tabular;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.AbstractParagraphBlock;

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
		ArrayList<TableItem> items = new ArrayList<TableItem>();
		for (AbstractItem item : super.getItems()) {
			items.add((TableItem) item);
		}

		return items;
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
