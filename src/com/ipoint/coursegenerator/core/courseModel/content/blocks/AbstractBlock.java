package com.ipoint.coursegenerator.core.courseModel.content.blocks;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.Convertable;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock.AbstractItem;

/**
 * Abstract block which includes items. These can't have empty items.
 * 
 * @author Kalashnikov Vladislav
 *
 * @param <T>
 *            Type of item that included in block
 */
public abstract class AbstractBlock<T extends AbstractItem<?>> implements Convertable {

	public static abstract class AbstractItem<T extends Object> implements Convertable {

		protected T value;

		protected AbstractItem(T value) {
			if (this.setValue(value)) {
				// TODO: exception - illegal value for block item
			}
		}

		protected AbstractItem() {
			this.value = null;
		}

		/**
		 * Returns value of item
		 * 
		 * @return value of item
		 */
		public T getValue() {
			return this.value;
		}

		/**
		 * Set value for item
		 * 
		 * @param value
		 *            Value for item
		 * @return If successful then true
		 */
		public boolean setValue(T value) {
			if (value == null) {
				return false;
			} else {
				this.value = value;
				return true;
			}
		}

	}

	private List<T> items;

	protected AbstractBlock(List<T> items) {
		if (items != null) {
			if (items.isEmpty()) {
				// TODO: exception empty list items
			} else {
				this.items = new ArrayList<>(items);
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
		return new ArrayList<>(this.items);
	}

}
