package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.tabular;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * Table block which includes rows
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TableBlock extends AbstractSectionBlock<TableItem> {

	public TableBlock(List<TableItem> rows) throws BlockCreationException {
		super(rows);
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

	@Override
	public String getText() {
		StringBuilder text = new StringBuilder();
		for (TableItem row : this.getItems()) {
			text.append(row.getText()).append('\n');
		}

		return Tools.removeExtraSpaces(text.toString()).trim();
	}

	@Override
	public List<TableItem> getItems() {
		return super.getItems();
	}

}
