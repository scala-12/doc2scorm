package com.ipoint.coursegenerator.core.courseModel.content.blocks;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.Convertable;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;

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

		protected AbstractItem(T value) throws ItemCreationException {
			if (!this.setValue(value)) {
				throw new ItemCreationException(this, value);
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
			boolean isValidValue = isValidValue(value);
			if (isValidValue) {
				this.value = value;
			}

			return isValidValue;
		}

		public boolean isValidValue(T value) {
			return (value != null);
		}

	}

	private final List<T> items;

	protected AbstractBlock(List<T> items) throws BlockCreationException {
		if (items != null) {
			if (items.isEmpty()) {
				throw new BlockCreationException(this, items);
			} else {
				int nullCount = items.stream().filter(item -> item == null).toArray().length;
				if (nullCount == 0) {
					this.items = new ArrayList<>(items);
				} else {
					throw new BlockCreationException(this, items, nullCount);
				}
			}
		} else {
			throw new BlockCreationException(this, items);
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
