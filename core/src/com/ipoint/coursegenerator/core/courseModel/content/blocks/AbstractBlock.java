package com.ipoint.coursegenerator.core.courseModel.content.blocks;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.Convertable;

/**
 * Abstract block which includes items. These can't have empty items.
 * 
 * @author Kalashnikov Vladislav
 *
 * @param <T>
 *            Type of item that included in block
 */
public abstract class AbstractBlock<T extends AbstractItem<?>> implements Convertable {

	private List<T> items;

	protected AbstractBlock(List<T> items) {
		if (items != null) {
			if (items.isEmpty()) {
				// TODO: exception empty list items
			} else {
				this.items = items;
			}
		} else {
			// TODO: exception null pointer value for block
		}
	}

	/**
	 * Returns first item of block
	 * 
	 * @return First item of block
	 */
	public T getFirstItem() {
		return this.getItems().get(0);
	}

	/**
	 * Returns all items of block
	 * 
	 * @return All items of block
	 */
	public List<T> getItems() {
		return this.items;
	}

}
