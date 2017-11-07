package com.ipoint.coursegenerator.core.courseModel.content.blocks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

		int index = -1;
		AbstractBlock<?> parent = null;

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

		public int getIndex() {
			return this.index;
		}

	}

	private final LinkedList<T> items;

	protected AbstractBlock(List<T> items) throws BlockCreationException {
		if (items != null) {
			LinkedList<T> checkedItems = new LinkedList<>(
					items.stream().filter(item -> item != null).collect(Collectors.toList()));
			if (checkedItems.isEmpty()) {
				throw new BlockCreationException(this, checkedItems);
			} else {
				int index = -1;
				for (T item : checkedItems) {
					index += 1;
					if (item.parent != null) {
						item.parent.items.remove(item);
					}
					item.parent = this;
					item.index = index;
				}

				this.items = checkedItems;
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
