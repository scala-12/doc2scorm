package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.Convertable;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.AbstractItem;

/**
 * Abstract Block. May be {@link TextBlock}, {@link TableBlock} ,
 * {@link ListBlock}, {@link HyperlinkBlock} or {@link ParagraphBlock}
 * 
 * @see TextBlock
 * @see TableBlock
 * @see ListBlock
 * @see HyperlinkBlock
 * @see ParagraphBlock
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractBlock implements Convertable {

	/**
	 * items of List block
	 */
	private List<? extends AbstractItem> items;

	/**
	 * Create block with items
	 * 
	 * @param items
	 *            Items of block
	 */
	protected AbstractBlock(List<? extends AbstractItem> items) {
		if (items != null) {
			if (items.isEmpty()) {
				// TODO:exception
			} else {
				this.items = items;
			}
		} else {
			// TODO: exception
		}
	}

	/**
	 * Returns all items of block
	 * 
	 * @return all items of block
	 */
	protected List<? extends AbstractItem> getItems() {
		return this.items;
	}

}
