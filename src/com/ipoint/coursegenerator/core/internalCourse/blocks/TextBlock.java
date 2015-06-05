package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;
import com.ipoint.coursegenerator.core.internalCourse.items.ImageItem;
import com.ipoint.coursegenerator.core.internalCourse.items.TextItem;

/**
 * Text block which may include images or text. This block is an extends of
 * {@link AbstractBlock}
 * 
 * @see AbstractBlock
 * @author Kalashnikov Vladislav
 *
 */
public class TextBlock extends AbstractBlock {

	/**
	 * @see AbstractBlock#AbstractBlock(List)
	 */
	public TextBlock(List<AbstractItem> items) {
		super(items);
	}

	/**
	 * @see AbstractBlock#AbstractBlock(AbstractItem)
	 */
	public TextBlock(AbstractItem item) {
		super(item);
	}

	@Override
	public boolean isRightItem(AbstractItem item) {
		if ((item.getClass().equals(TextItem.class))
				|| (item.getClass().equals(ImageItem.class))) {
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
