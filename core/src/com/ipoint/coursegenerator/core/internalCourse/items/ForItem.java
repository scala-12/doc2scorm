package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.List;

/**
 * Interface for items of block
 * 
 * @author Kalashnikov Vladislav
 *
 */
public interface ForItem {

	/**
	 * Returns value of block
	 * 
	 * @return Value of block
	 */
	public Object getValue();

	/**
	 * Change value of block
	 * 
	 * @param value
	 *            New value for block
	 * @return True if success
	 */
	public boolean setValue(Object value);

	/**
	 * Returns available List of classes for value of this Item
	 * 
	 * @return Available List of classes for value of this Item or null if class
	 *         may be anything
	 */
	public List<Class<?>> getAvailableValueClasses();

}
