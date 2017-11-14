package com.ipoint.coursegenerator.core.courseModel.content.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
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

	private final static Logger LOG = Logger.getLogger(AbstractBlock.class.getName());

	public static abstract class AbstractItem<T extends Object> implements Convertable {

		protected T value;

		int index = -1;
		AbstractBlock<?> parent = null;

		protected AbstractItem(T value) throws ItemCreationException {
			if (!this.setValue(value)) {
				throw new ItemCreationException(this, value);
			}
		}

		/**
		 * Returns value of item
		 * 
		 * @return value of item
		 */
		public final T getValue() {
			return this.value;
		}

		/**
		 * Set value for item
		 * 
		 * @param value
		 *            Value for item
		 * @return If successful then true
		 */
		public final boolean setValue(T value) {
			boolean isValidValue = isValidValue(value);
			if (isValidValue) {
				this.value = value;
			}

			return isValidValue;
		}

		public boolean isValidValue(T value) {
			return (value != null);
		}

		public final int getIndex() {
			return this.index;
		}

	}

	private final LinkedList<T> items;

	protected AbstractBlock(T item) throws BlockCreationException {
		this(Collections.singletonList(item));
	}

	protected AbstractBlock(List<T> items) throws BlockCreationException {
		if (items != null) {
			Map<Boolean, List<T>> nullAndItems = items.stream()
					.collect(Collectors.partitioningBy(item -> item == null));

			if (nullAndItems.get(false).isEmpty()) {
				throw new BlockCreationException(this, nullAndItems.get(false));
			} else {
				LinkedList<T> checkedItems = new LinkedList<>(nullAndItems.get(false));

				if (!nullAndItems.get(true).isEmpty()) {
					LOG.warning("Block has nullable items on create");
				}

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
	 * Returns all items of block
	 * 
	 * @return All items of block
	 */
	protected List<T> getItems() {
		return new ArrayList<>(this.items);
	}

	/** @return Item of block */
	protected T getItem() {
		return this.items.getFirst();
	}

}
