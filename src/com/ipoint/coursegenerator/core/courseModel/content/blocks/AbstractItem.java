package com.ipoint.coursegenerator.core.courseModel.content.blocks;

import com.ipoint.coursegenerator.core.courseModel.Convertable;

/**
 * Abstract item. May be uses in {@link AbstractBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 * @param <T>
 *            Type of value that included in item
 */
public abstract class AbstractItem<T extends Object> implements Convertable {

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
