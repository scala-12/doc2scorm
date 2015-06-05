package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;
import com.ipoint.coursegenerator.core.internalCourse.items.TableRowItem;

/**
 * Table block which includes rows of table. This block is an extends of
 * {@link AbstractBlock}
 * 
 * @see AbstractBlock
 * @author Kalashnikov Vladislav
 *
 */
public class TableBlock extends AbstractBlock {

	/**
	 * @see AbstractBlock#AbstractBlock(List)
	 */
	public TableBlock(List<AbstractItem> items) {
		super(items);
	}

	/**
	 * @see AbstractBlock#AbstractBlock(AbstractItem)
	 */
	public TableBlock(AbstractItem item) {
		super(item);
	}

	@Override
	public boolean isRightItem(AbstractItem item) {
		if (item.getClass().equals(TableRowItem.class)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Node toHtml() {
		// TODO Auto-generated method stub

		return null;
	}

}
