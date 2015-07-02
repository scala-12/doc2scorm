package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.TableCellItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.TableItem;

/**
 * Table block which includes rows of table. This block is an extends of
 * {@link AbstractBlock}
 * 
 * @see AbstractBlock
 * @author Kalashnikov Vladislav
 *
 */
public class TableBlock extends AbstractParagraphBlock {

	/**
	 * Create Table
	 * 
	 * @param rows
	 *            Rows of table
	 */
	public TableBlock(List<TableItem> rows) {
		super(rows);
	}

	@Override
	public List<TableItem> getItems() {
		return (List<TableItem>) super.getItems();
	}

	@Override
	public Element toHtml(Document creatorTags) {
		Element table = creatorTags.createElement("table");
		Element tBody = creatorTags.createElement("tbody");
		table.appendChild(tBody);

		for (TableItem row : this.getItems()) {
			Element tRow = creatorTags.createElement("tr");
			tBody.appendChild(tRow);
			for (TableCellItem cell : row.getValue()) {
				if ((cell.getColSpan() != null) && (cell.getRowSpan() != null)) {
					Element tCell = creatorTags.createElement("td");
					tRow.appendChild(tCell);
					tCell.setAttribute("colspan", cell.getColSpan().toString());
					tCell.setAttribute("rowspan", cell.getRowSpan().toString());
					if (cell.getValue() != null) {
						for (AbstractParagraphBlock par : cell.getValue()) {
							Element tPar = par.toHtml(creatorTags);
							tCell.appendChild(tPar);
						}
					}
				}
			}
		}

		return table;
	}
}
