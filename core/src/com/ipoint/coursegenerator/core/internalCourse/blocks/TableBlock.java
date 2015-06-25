package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.TableItem;

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
	public Node toHtml() {
		// TODO Auto-generated method stub

		return null;
	}

}
