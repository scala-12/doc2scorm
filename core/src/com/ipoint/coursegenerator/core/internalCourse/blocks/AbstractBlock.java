package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.internalCourse.Convertable;
import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;

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
public abstract class AbstractBlock implements Convertable, ForBlock {

	/**
	 * items of block
	 */
	private ArrayList<AbstractItem> items;

	/**
	 * Create block with items
	 * 
	 * @param items
	 *            list items for block
	 */
	public AbstractBlock(List<AbstractItem> items) {
		this.items = (ArrayList<AbstractItem>) this.checkedItems(items);
	}

	/**
	 * Creating block with one item
	 * 
	 * @param item
	 *            item for block
	 */
	public AbstractBlock(AbstractItem item) {
		this.items = new ArrayList<AbstractItem>();
		if (item != null) {
			this.addItem(this.checkedItem(item));
		}
	}

	@Override
	public boolean isRightItem(AbstractItem item) {
		return true;
	}

	/**
	 * Checking block
	 * 
	 * @return Checked block
	 */
	private AbstractItem checkedItem(AbstractItem item) {
		if (this.isRightItem(item)) {
			return item;
		} else {
			// TODO: Exception
			return null;
		}
	}

	/**
	 * Checking blocks
	 * 
	 * @return Checked blocks
	 */
	private List<AbstractItem> checkedItems(List<AbstractItem> items) {
		for (AbstractItem item : items) {
			this.checkedItem(item);
		}
		return items;
	}

	/**
	 * Returns all items of block
	 * 
	 * @return all items of block
	 */
	public List<AbstractItem> getItems() {
		return this.items;
	}

	/**
	 * Appends the item to the block
	 * 
	 * @param item
	 *            item for adding
	 * @return true
	 */
	public boolean addItem(AbstractItem item) {
		return (this.isRightItem(item)) ? this.items.add(item) : false;
	}

	/**
	 * Returns the item at the specified position in this block
	 * 
	 * @param index
	 *            index of the element to return
	 * @return item of block at the the specified position
	 */
	public AbstractItem getItem(int index) {
		return ((this.items.size() <= index) || this.items.isEmpty()) ? null
				: this.items.get(index);
	}

}
