package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.ArrayList;
import java.util.List;

/**
 * Item for block
 * 
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractItem implements ForItem {

	private Object value;

	/**
	 * Create item with preset value
	 * @param value Value of This item
	 */
	public AbstractItem(Object value) {
		if (!this.setValue(value)) {
			// TODO: exception
		}
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public boolean setValue(Object value) {
		if (this.isRightItemValue(value)) {
			this.value = value;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checking: this value is right for This item
	 * @param value Value for item
	 * @return true if value is right for This item
	 */
	@SuppressWarnings("unchecked")
	protected boolean isRightItemValue(Object value) {
		if (value != null) {
			if (getAvailableValueClasses() == null) {
				return true;
			} else {
				boolean result = false;
				if ((value.getClass().equals(List.class)) || (value.getClass().equals(ArrayList.class))) {
					result = false;
					for (Object obj : (List<Object>) value) {
						for (Class<?> clazz : getAvailableValueClasses()) {
							if (obj.getClass().equals(clazz)) {
								result = true;
							}
						}
					}
					if (!result) {
						return false;
					}
				} else {
					for (Class<?> clazz : getAvailableValueClasses()) {
						if (value.getClass().equals(clazz)) {
							return true;
						}
					}
				}
				return result;
			}
		} else {
			return false;
		}
	}

	@Override
	public List<Class<?>> getAvailableValueClasses() {
		return null;
	}

}
